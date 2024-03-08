package com.pathfind.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Dto.FindPathRoom;

import java.io.IOException;
import java.util.List;

public interface FindPathRoomService {
    public FindPathRoom findById(String roomId) throws IOException;

    public FindPathRoom createRoom(String name) throws JsonProcessingException;

    public FindPathRoom changeRoomMemberLocation(String roomId, String sender, String message) throws IOException, JsonProcessingException;

    public List<List<ShortestPathRoute>> findRoadShortestRoute(FindPathRoom findPathRoom);

    public List<List<ShortestPathRoute>> findSidewalkShortestRoute(FindPathRoom findPathRoom);

    public void deleteRoom(String roomId);
}
