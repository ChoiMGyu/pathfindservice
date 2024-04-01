/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2) 인터페이스
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Domain.FindPathRoom;
import com.pathfind.system.findPathService2Domain.MemberLatLng;
import com.pathfind.system.findPathService2Domain.RoomMemberType;
import com.pathfind.system.findPathService2Domain.TransportationType;

import java.io.IOException;
import java.util.List;

public interface FindPathRoomService {
    public List<FindPathRoom> findAllRoom();

    public FindPathRoom findRoomById(String roomId) throws IOException;

    public FindPathRoom createRoom(String nickname, String roomName) throws JsonProcessingException;

    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, MemberLatLng memberLatLng) throws IOException;

    public List<List<ShortestPathRoute>> findRoadShortestRoute(FindPathRoom findPathRoom);

    public List<List<ShortestPathRoute>> findSidewalkShortestRoute(FindPathRoom findPathRoom);

    public void deleteRoom(String roomId);

    public FindPathRoom inviteMember(String roomId, String nickname) throws IOException;

    public boolean checkMemberInvited(String roomId, String nickname) throws IOException;

    public void memberEnterRoom(String roomId, String nickname, RoomMemberType roomMemberType, TransportationType transportationType) throws IOException;

    public FindPathRoom leaveRoom(String nickname) throws IOException;

    public FindPathRoom findCurRoomByNickname(String nickname);
}
