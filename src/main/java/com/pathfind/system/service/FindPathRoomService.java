package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.findPathService2Dto.FindPathRoom;

import java.io.IOException;

public interface FindPathRoomService {
    public FindPathRoom findRoomById(String roomId) throws IOException;

    public FindPathRoom createRoom(String nickname, String roomName) throws JsonProcessingException;

    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, String message) throws IOException, JsonProcessingException;

    public String findRoadShortestRoute(FindPathRoom findPathRoom) throws JsonProcessingException;

    public String findSidewalkShortestRoute(FindPathRoom findPathRoom) throws JsonProcessingException;

    public void deleteRoom(String roomId);

    public void inviteMember(String roomId, String nickname) throws IOException;
}
