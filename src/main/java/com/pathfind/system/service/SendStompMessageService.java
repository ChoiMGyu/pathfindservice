package com.pathfind.system.service;

import com.pathfind.system.findPathDto.ShortestPathRoute;

import java.util.List;

public interface SendStompMessageService {

    void sendEnter(String roomId, String sender, String message);

    void sendLeave(String roomId, String sender, String manager, String message);

    void sendExpired(String roomId, String message);

    void sendRoute(String roomId, String sender, List<List<ShortestPathRoute>> route);

    void sendNotInCampus(String roomId, String sender, String message);
}
