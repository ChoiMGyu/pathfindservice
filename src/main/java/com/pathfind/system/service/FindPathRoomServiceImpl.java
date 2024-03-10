package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.algorithm.Dijkstra;
import com.pathfind.system.algorithm.DijkstraResult;
import com.pathfind.system.algorithm.Graph;
import com.pathfind.system.algorithm.Node;
import com.pathfind.system.domain.*;
import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Dto.FindPathRoom;
import com.pathfind.system.findPathService2Dto.MemberLatLng;
import com.pathfind.system.repository.RoadEdgeRepository;
import com.pathfind.system.repository.RoadVertexRepository;
import com.pathfind.system.repository.SidewalkEdgeRepository;
import com.pathfind.system.repository.SidewalkVertexRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public FindPathRoom findRoomById(String roomId) throws IOException {
        logger.info("get room, roomId: {}", roomId);
        String jsonStringRoom = redisUtil.getData(roomId);
        return objectMapper.readValue(jsonStringRoom, FindPathRoom.class);
    }

    @Override
    public FindPathRoom createRoom(String nickname, String roomName) throws JsonProcessingException {
        FindPathRoom findPathRoom = FindPathRoom.createFindPathRoom(roomName);
        while (redisUtil.getData(findPathRoom.getRoomId()) != null) findPathRoom.createRoomId();
        findPathRoom.pushNewMember(nickname, null, null, false);

        String jsonStringRoom = objectMapper.writeValueAsString(findPathRoom);
        logger.info("create jsonStringRoom: {}", jsonStringRoom);

        redisUtil.setData(findPathRoom.getRoomId(), jsonStringRoom);
        return findPathRoom;
    }

    @Override
    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, String message) throws IOException {
        logger.info("change room member's location, roomId {}", roomId);
        FindPathRoom findPathRoom = findRoomById(roomId);
        MemberLatLng memberLatLng = objectMapper.readValue(message, MemberLatLng.class);
        double memberLat = memberLatLng.getLatitude(), memberLng = memberLatLng.getLongitude(), dist = 1000000.0;
        Integer senderIdx = findPathRoom.getMemberNickname().indexOf(sender), closestVertexId = -1;

        logger.info("member({})'s current location - latitude: {}, longitude: {}", senderIdx, memberLat, memberLng);
        logger.info("find closest vertexId...");
        if (!findPathRoom.getIsRoad().get(senderIdx)) {
            List<SidewalkVertex> SWVertices = sidewalkVertexRepository.findAll();
            for (int i = 0; i < SWVertices.size(); i++) {
                SidewalkVertex vertex = SWVertices.get(i);
                double tmpDist = Math.pow(Math.abs(memberLat - vertex.getLatitude()), 2) + Math.pow(Math.abs(memberLng - vertex.getLongitude()), 2);
                if (dist > tmpDist) {
                    logger.info("vertexId: {}, distance difference: {}, minimum distance: {}", vertex.getId() - 1, tmpDist, dist);
                    dist = tmpDist;
                    closestVertexId = vertex.getId().intValue() - 1;
                }
            }
        } else {
            List<RoadVertex> RVertices = roadVertexRepository.findAll();
            for (int i = 0; i < RVertices.size(); i++) {
                RoadVertex vertex = RVertices.get(i);
                double tmpDist = Math.pow(Math.abs(memberLat - vertex.getLatitude()), 2) + Math.pow(Math.abs(memberLng - vertex.getLongitude()), 2);
                if (dist > tmpDist) {
                    logger.info("vertexId: {}, distance difference: {}, minimum distance: {}", vertex.getId() - 1, tmpDist, dist);
                    dist = tmpDist;
                    closestVertexId = vertex.getId().intValue() - 1;
                }
            }
        }
        logger.info("member({})'s closest vertexId: {}", senderIdx, closestVertexId);
        findPathRoom.changeMemberLocation(senderIdx, memberLatLng, closestVertexId.longValue());
        String jsonStringRoom = objectMapper.writeValueAsString(findPathRoom);
        redisUtil.setData(roomId, jsonStringRoom);
        return findPathRoom;
    }

    @Override
    public String findRoadShortestRoute(FindPathRoom findPathRoom) throws JsonProcessingException {
        Long start = findPathRoom.getClosestVertexId().get(0);
        MemberLatLng startLatLng = findPathRoom.getMemberLocation().get(0);
        List<RoadVertex> vertices = roadVertexRepository.findAll();
        int numVertices = vertices.size();
        logger.info("roadVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<RoadEdge> edges = roadEdgeRepository.findAll();
        logger.info("roadEdges size: {}", edges.size());
        for (RoadEdge edge : edges) {
            logger.info("edge 정보 : " + edge.getRoadVertex1() + " " + edge.getRoadVertex2() + " " + edge.getLength());
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
        logger.info("방장과 각 인원들의 경로 계산");
        for (int i = 1; i < findPathRoom.getClosestVertexId().size(); i++) {
            Long end = findPathRoom.getClosestVertexId().get(i);
            logger.info("방장, {}번째 사람의 경로", end);
            MemberLatLng memberLatLng = findPathRoom.getMemberLocation().get(i);
            List<ShortestPathRoute> routeInfo = new ArrayList<>();
            logger.info("시작 위치 - latitude: {}, longitude: {}", startLatLng.getLatitude(), startLatLng.getLongitude());
            List<Integer> shortestRoute = Dijkstra.getShortestRoute(dijkstraResult.getPath(), start, end);
            logger.info("끝 위치 - latitude: {}, longitude: {}", memberLatLng.getLatitude(), memberLatLng.getLongitude());
            routeInfo.add(new ShortestPathRoute(-1L, startLatLng.getLatitude(), startLatLng.getLongitude()));
            for (Integer idx : shortestRoute) {
                routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
            }
            routeInfo.add(new ShortestPathRoute(-1L, memberLatLng.getLatitude(), memberLatLng.getLongitude()));
            shortestRouteList.add(routeInfo);
        }

        return objectMapper.writeValueAsString(shortestRouteList);
    }

    @Override
    public String findSidewalkShortestRoute(FindPathRoom findPathRoom) throws JsonProcessingException {
        Long start = findPathRoom.getClosestVertexId().get(0);
        MemberLatLng startLatLng = findPathRoom.getMemberLocation().get(0);
        List<SidewalkVertex> vertices = sidewalkVertexRepository.findAll();
        int numVertices = vertices.size();
        logger.info("sidewalkVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<SidewalkEdge> edges = sidewalkEdgeRepository.findAll();
        logger.info("sidewalkEdges size: {}", edges.size());
        for (SidewalkEdge edge : edges) {
            logger.info("edge 정보 : " + edge.getSidewalkVertex1() + " " + edge.getSidewalkVertex2() + " " + edge.getLength());
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
        logger.info("방장과 각 인원들의 경로 계산");
        for (int i = 1; i < findPathRoom.getClosestVertexId().size(); i++) {
            Long end = findPathRoom.getClosestVertexId().get(i);
            logger.info("방장, {}번째 사람의 경로", end);
            List<ShortestPathRoute> routeInfo = new ArrayList<>();
            MemberLatLng memberLatLng = findPathRoom.getMemberLocation().get(i);
            logger.info("시작 위치 - latitude: {}, longitude: {}", startLatLng.getLatitude(), startLatLng.getLongitude());
            List<Integer> shortestRoute = Dijkstra.getShortestRoute(dijkstraResult.getPath(), start, end);
            logger.info("끝 위치 - latitude: {}, longitude: {}", memberLatLng.getLatitude(), memberLatLng.getLongitude());
            routeInfo.add(new ShortestPathRoute(-1L, startLatLng.getLatitude(), startLatLng.getLongitude()));
            for (Integer idx : shortestRoute) {
                routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
            }
            routeInfo.add(new ShortestPathRoute(-1L, memberLatLng.getLatitude(), memberLatLng.getLongitude()));
            shortestRouteList.add(routeInfo);
        }

        return objectMapper.writeValueAsString(shortestRouteList);
    }

    @Override
    public void deleteRoom(String roomId) {
        logger.info("delete room, id: {}", roomId);
        redisUtil.deleteData(roomId);
    }

    @Override
    public void inviteMember(String roomId, String nickname) throws IOException {
        FindPathRoom room = findRoomById(roomId);
        room.pushNewMember(nickname, null, null, false);
        String jsonStringRoom = objectMapper.writeValueAsString(room);
        redisUtil.setData(roomId, jsonStringRoom);
    }
}
