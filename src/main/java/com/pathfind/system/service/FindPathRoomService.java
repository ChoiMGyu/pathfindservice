/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2) 인터페이스
 * 최근 수정 일자 : 2024.04.07(일)
 */
package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Domain.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface FindPathRoomService {
    public List<FindPathRoom> findAllRoom();

    public FindPathRoom findRoomById(String roomId) throws IOException;

    public ArrayList<RoomMemberInfo> getCurRoomList(String roomId) throws IOException;

    public ArrayList<String> getRoomInviteList(String roomId) throws IOException;

    public ArrayList<String> deleteListUser(String roomId, String nickname) throws IOException;

    //public void changeOwnerName(String roomId, String nickname) throws IOException;

    public FindPathRoom createRoom(String nickname, String roomName, TransportationType transportationType) throws JsonProcessingException;

    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, MemberLatLng memberLatLng) throws IOException;

    public List<List<ShortestPathRoute>> findRoadShortestRoute(FindPathRoom findPathRoom);

    public List<List<ShortestPathRoute>> findSidewalkShortestRoute(FindPathRoom findPathRoom);

    public void deleteRoom(String roomId);

    public FindPathRoom inviteMember(String roomId, String nickname) throws IOException;

    public boolean checkMemberInvited(String roomId, String nickname) throws IOException;

    public boolean checkMemberCur(String roomId, String nickname) throws IOException;

    public void memberEnterRoom(String roomId, String nickname) throws IOException;

    public FindPathRoom leaveRoom(String nickname) throws IOException;

    public FindPathRoom leaveRoom(String nickname, String roomId) throws IOException;

    public FindPathRoom findCurRoomByNickname(String nickname);
}
