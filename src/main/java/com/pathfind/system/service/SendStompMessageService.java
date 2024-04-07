/*
 * 클래스 기능 : Stomp 메시지 전송 서비스 인터페이스
 * 최근 수정 일자 : 2024.04.07(월)
 */
package com.pathfind.system.service;

import com.pathfind.system.findPathDto.ShortestPathRoute;

import java.time.LocalDateTime;
import java.util.List;

public interface SendStompMessageService {

    void sendEnter(String roomId, String sender, String message, int curMemberNum, LocalDateTime roomRemainingTime);

    void sendLeave(String roomId, String sender, String manager, String message, int curMemberNum, LocalDateTime roomRemainingTime);

    void sendExpired(String roomId, String message);

    void sendRoute(String roomId, String sender, List<List<ShortestPathRoute>> route);

    void sendNotInCampus(String roomId, String sender, String message);
}
