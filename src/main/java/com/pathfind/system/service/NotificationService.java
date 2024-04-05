/*
 * 클래스 기능 : 알림 서비스 인터페이스
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.service;

import com.pathfind.system.notificationServiceDomain.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    public SseEmitter subscribe(String nickname);

    public void sendInviteNotification(String content, String sender, String receiver, NotificationType notificationType, String url, String roomId);

    public void sendAllNotificationByNickname(String sseEmitterId, SseEmitter sseEmitter, String nickname);

    public void deleteAllNotificationByRoomId(String roomId);

    public void changeAllReadTypeToReadByNickname(String nickname);

    void deleteNotificationByRoomIdAndNickname(String roomId, String nickname);
}
