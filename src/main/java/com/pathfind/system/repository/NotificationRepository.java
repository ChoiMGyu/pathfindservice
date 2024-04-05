/*
 * 클래스 기능 : 알림 레포지토리 인터페이스
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.repository;

import com.pathfind.system.notificationServiceDomain.Notification;

import java.util.List;

public interface NotificationRepository {

    public void saveNotificationCache(String nickname, Notification notification);

    public void saveAllNotificationCache(String nickname, List<Notification> notifications);

    public List<Notification> findAllNotificationCacheByNickname(String nickname);

    public void deleteAllNotificationCacheByNickname(String nickname);

    public void deleteAllNotificationCacheByRoomId(String roomId);

    public void deleteNotificationByRoomIdAndNickname(String roomId, String nickname);
}
