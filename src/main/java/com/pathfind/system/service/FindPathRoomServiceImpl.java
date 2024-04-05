/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2) 구현체
 * 최근 수정 일자 : 2024.04.04(월)
 */
package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.algorithm.Dijkstra;
import com.pathfind.system.algorithm.DijkstraResult;
import com.pathfind.system.algorithm.Graph;
import com.pathfind.system.algorithm.Node;
import com.pathfind.system.domain.*;
import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Domain.*;
import com.pathfind.system.repository.RoadEdgeRepository;
import com.pathfind.system.repository.RoadVertexRepository;
import com.pathfind.system.repository.SidewalkEdgeRepository;
import com.pathfind.system.repository.SidewalkVertexRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class FindPathRoomServiceImpl implements FindPathRoomService {

    private static final Logger logger = LoggerFactory.getLogger(FindPathRoomServiceImpl.class);

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final RoadEdgeRepository roadEdgeRepository;
    private final RoadVertexRepository roadVertexRepository;
    private final SidewalkEdgeRepository sidewalkEdgeRepository;
    private final SidewalkVertexRepository sidewalkVertexRepository;
    private final NotificationService notificationService;

    @Override
    public List<FindPathRoom> findAllRoom() {
        logger.info("Get all room");
        ValueOperations<String, String> allData = redisUtil.getAllData();
        Set<String> keys = allData.getOperations().keys("*");
        if (keys == null) return null;
        List<FindPathRoom> rooms = new ArrayList<>();
        for (String key : keys) {
            try {
                rooms.add(objectMapper.readValue(allData.get(key), FindPathRoom.class));
            } catch (JsonProcessingException e) {
                logger.warn(e.getMessage());
            }
        }
        logger.info("All room data: ");
        for (FindPathRoom room : rooms) {
            logger.info("{}", room);
        }
        return rooms;
    }

    @Override
    public FindPathRoom findRoomById(String roomId) throws IOException {
        logger.info("Get room, roomId: {}", roomId);
        String jsonStringRoom = redisUtil.getData(roomId);
        return jsonStringRoom == null ? null : objectMapper.readValue(jsonStringRoom, FindPathRoom.class);
    }

    @Override
    public ArrayList<String> getCurRoomList(String roomId) throws IOException {
        logger.info("roomId {}에 있는 현재 유저들의 리스트 반환", roomId);
        ArrayList<String> result = new ArrayList<>();

        FindPathRoom findPathRoom = findRoomById(roomId);
        for(int i = 0; i < findPathRoom.getCurMember().size(); i++) {
            result.add(findPathRoom.getCurMember().get(i).getNickname());
        }

        return result;
    }

    @Override
    public ArrayList<String> getRoomInviteList(String roomId) throws IOException {
        logger.info("roomId {}에 있는 초대 유저들의 리스트 반환", roomId);

        FindPathRoom findPathRoom = findRoomById(roomId);

        return new ArrayList<>(findPathRoom.getInvitedMember());
    }

    @Override
    public void deleteListUser(String roomId, String nickname) throws IOException {
        logger.info("roomId {}에 있는 nickname {}인 유저를 현재 인원에서 삭제", roomId, nickname);

        FindPathRoom findPathRoom = findRoomById(roomId);

        findPathRoom.leaveRoomCurMember(nickname);
    }

    @Override
    public FindPathRoom createRoom(String nickname, String roomName, TransportationType transportationType) throws JsonProcessingException {
        logger.info("Create room...");
        FindPathRoom findPathRoom = FindPathRoom.createFindPathRoom(roomName, transportationType);
        while (redisUtil.getData(findPathRoom.getRoomId()) != null) findPathRoom.createRoomId();
        pushManagerInvited(findPathRoom, nickname);

        String jsonStringRoom = objectMapper.writeValueAsString(findPathRoom);
        logger.info("Create jsonStringRoom: {}", jsonStringRoom);

        redisUtil.setDataExpire(findPathRoom.getRoomId(), jsonStringRoom, RoomValue.ROOM_DURATION);
        return findPathRoom;
    }

    public void pushManagerInvited(FindPathRoom room, String nickname) {
        room.pushNewMember(nickname);
        room.changeOwnerName(nickname);
    }

    @Override
    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, MemberLatLng memberLatLng) throws IOException {
        //logger.info("Change room member's location, roomId {}", roomId);
        FindPathRoom room = findRoomById(roomId);
        if(room == null) return null;

        room.changeMemberLocation(sender, memberLatLng);
        double memberLat = memberLatLng.getLatitude(), memberLng = memberLatLng.getLongitude(), dist = 1000000.0;

        //logger.info("Member({})'s current location - latitude: {}, longitude: {}", sender, memberLat, memberLng);
        //logger.info("Find closest vertexId...");
        if (room.getTransportationType() == TransportationType.SIDEWALK) {
            List<SidewalkVertex> SWVertices = sidewalkVertexRepository.findAll();
            for (SidewalkVertex vertex : SWVertices) {
                double tmpDist = Math.pow(Math.abs(memberLat - vertex.getLatitude()), 2) + Math.pow(Math.abs(memberLng - vertex.getLongitude()), 2);
                if (dist > tmpDist) {
                    //logger.info("VertexId: {}, distance difference: {}, minimum distance: {}", vertex.getId() - 1, tmpDist, dist);
                    dist = tmpDist;
                    room.changeMemberClosestVertexId(sender, vertex.getId() - 1);
                }
            }
        } else {
            List<RoadVertex> RVertices = roadVertexRepository.findAll();
            for (RoadVertex vertex : RVertices) {
                double tmpDist = Math.pow(Math.abs(memberLat - vertex.getLatitude()), 2) + Math.pow(Math.abs(memberLng - vertex.getLongitude()), 2);
                if (dist > tmpDist) {
                    //logger.info("VertexId: {}, distance difference: {}, minimum distance: {}", vertex.getId() - 1, tmpDist, dist);
                    dist = tmpDist;
                    room.changeMemberClosestVertexId(sender, vertex.getId() - 1);
                }
            }
        }
        //logger.info("Member({})'s closest vertexId: {}", sender, room.findMemberByNickname(sender).getClosestVertexId());
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(roomId, jsonStringRoom);
        return room;
    }

    @Override
    public List<List<ShortestPathRoute>> findRoadShortestRoute(FindPathRoom findPathRoom) {
        Long start = findPathRoom.getOwner().getClosestVertexId();
        MemberLatLng startLatLng = findPathRoom.getOwner().getLocation();
        List<RoadVertex> vertices = roadVertexRepository.findAll();
        int numVertices = vertices.size();
        //logger.info("RoadVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<RoadEdge> edges = roadEdgeRepository.findAll();
        //logger.info("RoadEdges size: {}", edges.size());
        for (RoadEdge edge : edges) {
            //logger.info("Edge 정보 : " + edge.getRoadVertex1() + " " + edge.getRoadVertex2() + " " + edge.getLength());
            Objects object = vertices.get(Math.toIntExact(edge.getRoadVertex2() - 1)).getObject();
            boolean isBuilding = object != null && object.getObjectType() == ObjType.BUILDING;
            graph.addEdge(edge.getRoadVertex1() - 1, edge.getRoadVertex2() - 1, edge.getLength(), isBuilding);
        }
        List<Node> nodes = new ArrayList<>();
        for (RoadVertex roadVertex : vertices) {
            Objects object = roadVertex.getObject();
            boolean isBuilding = object != null && object.getObjectType() == ObjType.BUILDING;
            nodes.add(new Node(roadVertex.getId() - 1, 0, isBuilding));
        }
        DijkstraResult dijkstraResult = Dijkstra.dijkstra(nodes, graph, start, -1L);
        List<List<ShortestPathRoute>> shortestRouteList = new ArrayList<>();
        //logger.info("방장과 각 인원들의 경로 계산");
        for (int i = 1; i < findPathRoom.getCurMember().size(); i++) {
            Long end = findPathRoom.getCurMember().get(i).getClosestVertexId();
            if (end == null) continue;
            //logger.info("방장, {}번째 사람의 경로", end);
            MemberLatLng memberLatLng = findPathRoom.getCurMember().get(i).getLocation();
            List<ShortestPathRoute> routeInfo = new ArrayList<>();
            //logger.info("시작 위치 - latitude: {}, longitude: {}", startLatLng.getLatitude(), startLatLng.getLongitude());
            List<Integer> shortestRoute = Dijkstra.getShortestRoute(dijkstraResult.getPath(), start, end);
            //logger.info("끝 위치 - latitude: {}, longitude: {}", memberLatLng.getLatitude(), memberLatLng.getLongitude());
            routeInfo.add(new ShortestPathRoute(-1L, startLatLng.getLatitude(), startLatLng.getLongitude()));
            for (Integer idx : shortestRoute) {
                routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
            }
            routeInfo.add(new ShortestPathRoute(-1L, memberLatLng.getLatitude(), memberLatLng.getLongitude()));
            shortestRouteList.add(routeInfo);
        }

        return shortestRouteList;
    }

    @Override
    public List<List<ShortestPathRoute>> findSidewalkShortestRoute(FindPathRoom findPathRoom) {
        Long start = findPathRoom.getOwner().getClosestVertexId();
        MemberLatLng startLatLng = findPathRoom.getOwner().getLocation();
        List<SidewalkVertex> vertices = sidewalkVertexRepository.findAll();
        int numVertices = vertices.size();
        //logger.info("SidewalkVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<SidewalkEdge> edges = sidewalkEdgeRepository.findAll();
        //logger.info("SidewalkEdges size: {}", edges.size());
        for (SidewalkEdge edge : edges) {
            //logger.info("edge 정보 : " + edge.getSidewalkVertex1() + " " + edge.getSidewalkVertex2() + " " + edge.getLength());
            Objects object = vertices.get(Math.toIntExact((edge.getSidewalkVertex2() - 1))).getObject();
            boolean isBuilding = object != null && object.getObjectType() == ObjType.BUILDING;
            graph.addEdge(edge.getSidewalkVertex1() - 1, edge.getSidewalkVertex2() - 1, edge.getLength(), isBuilding);
        }
        List<Node> nodes = new ArrayList<>();
        for (SidewalkVertex sidewalkVertex : vertices) {
            Objects object = sidewalkVertex.getObject();
            boolean isBuilding = object != null && object.getObjectType() == ObjType.BUILDING;
            nodes.add(new Node(sidewalkVertex.getId() - 1, 0, isBuilding));
        }
        DijkstraResult dijkstraResult = Dijkstra.dijkstra(nodes, graph, start, -1L);
        List<List<ShortestPathRoute>> shortestRouteList = new ArrayList<>();
        //logger.info("방장과 각 인원들의 경로 계산");
        for (int i = 1; i < findPathRoom.getCurMember().size(); i++) {
            Long end = findPathRoom.getCurMember().get(i).getClosestVertexId();
            if (end == null) continue;
            //logger.info("방장, {}번째 사람의 경로", end);
            List<ShortestPathRoute> routeInfo = new ArrayList<>();
            MemberLatLng memberLatLng = findPathRoom.getCurMember().get(i).getLocation();
            //logger.info("시작 위치 - latitude: {}, longitude: {}", startLatLng.getLatitude(), startLatLng.getLongitude());
            List<Integer> shortestRoute = Dijkstra.getShortestRoute(dijkstraResult.getPath(), start, end);
            //logger.info("끝 위치 - latitude: {}, longitude: {}", memberLatLng.getLatitude(), memberLatLng.getLongitude());
            routeInfo.add(new ShortestPathRoute(-1L, startLatLng.getLatitude(), startLatLng.getLongitude()));
            for (Integer idx : shortestRoute) {
                routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
            }
            routeInfo.add(new ShortestPathRoute(-1L, memberLatLng.getLatitude(), memberLatLng.getLongitude()));
            shortestRouteList.add(routeInfo);
        }

        return shortestRouteList;
    }

    @Override
    public void deleteRoom(String roomId) {
        logger.info("Delete room, id: {}", roomId);
        notificationService.deleteAllNotificationByRoomId(roomId);
        redisUtil.deleteData(roomId);
    }

    @Override
    public FindPathRoom inviteMember(String roomId, String nickname) throws IOException {
        logger.info("Invite member {} to room, roomId: {}", nickname, roomId);
        FindPathRoom room = findRoomById(roomId);
        room.pushNewMember(nickname);
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(roomId, jsonStringRoom);
        return room;
    }

    @Override
    public boolean checkMemberInvited(String roomId, String nickname) throws IOException {
        logger.info("Check {} is already invited at roomId {}", nickname, roomId);
        FindPathRoom room = findRoomById(roomId);
        return room.checkMemberInvited(nickname);
    }

    @Override
    public boolean checkMemberCur(String roomId, String nickname) throws IOException {
        logger.info("Check {} is already connect at roomId {}", nickname, roomId);
        FindPathRoom room = findRoomById(roomId);
        return room.checkMemberCur(nickname);
    }

    @Override
    public void memberEnterRoom(String roomId, String nickname) throws IOException {
        logger.info("{} enter the room, roomId: {}", nickname, roomId);
        FindPathRoom room = findRoomById(roomId);
        if (room == null) return;
        room.enterRoom(nickname, room.getOwnerNickname().equals(nickname) ? RoomMemberType.OWNER : RoomMemberType.NORMAL, null, null);
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(room.getRoomId(), jsonStringRoom);
    }

    @Override
    public FindPathRoom leaveRoom(String nickname) throws IOException {
        FindPathRoom room = findCurRoomByNickname(nickname);
        if (room == null) return null;
        String roomId = room.getRoomId();
        RoomMemberInfo member = room.findMemberByNickname(nickname);
        logger.info("{} leaves the room, roomId: {}", member.getNickname(), roomId);
        room.leaveRoomCurMember(nickname);
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(roomId, jsonStringRoom);
        return room;
    }

    @Override
    public FindPathRoom leaveRoom(String nickname, String roomId) throws IOException {
        FindPathRoom room = findRoomById(roomId);
        if (room==null || !room.checkMemberCur(nickname)) return null;
        RoomMemberInfo member = room.findMemberByNickname(nickname);
        logger.info("{} leaves the room, roomId: {}", member.getNickname(), roomId);
        room.leaveRoomCurMember(nickname);
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(roomId, jsonStringRoom);
        return room;
    }

    @Override
    public FindPathRoom findCurRoomByNickname(String nickname) {
        logger.info("Find room by nickname");
        List<FindPathRoom> rooms = findAllRoom();
        FindPathRoom result = null;
        for (FindPathRoom room : rooms) {
            if (room.checkMemberCur(nickname)) {
                result = room;
                break;
            }
        }
        return result;
    }
}
