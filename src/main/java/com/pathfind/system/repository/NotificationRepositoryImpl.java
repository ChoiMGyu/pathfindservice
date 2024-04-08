/*
 * 클래스 기능 : 알림 리포지토리 구현체
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.notificationServiceDomain.Notification;
import com.pathfind.system.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private static final Logger logger = LoggerFactory.getLogger(NotificationRepositoryImpl.class);

    private final RedisUtil redisUtil;

    private final ObjectMapper objectMapper;

    @Override
    public void saveNotificationCache(String nickname, Notification notification) {
        logger.info("Save notification...");
        List<Notification> notifications = findAllNotificationCacheByNickname(nickname);
        notifications.add(notification);
        try {
            String jsonStringNotifications = objectMapper.writeValueAsString(notifications);
            redisUtil.setData(nickname + "Notification", jsonStringNotifications);
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }

    @Override
    public void saveAllNotificationCache(String nickname, List<Notification> notifications) {
        logger.info("Save all notifications...");
        try {
            String jsonStringNotifications = objectMapper.writeValueAsString(notifications);
            redisUtil.setData(nickname + "Notification", jsonStringNotifications);
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }

    @Override
    public List<Notification> findAllNotificationCacheByNickname(String nickname) {
        logger.info("Get all notifications by nickname");
        String jsonStringNotifications = redisUtil.getData(nickname + "Notification");
        try {
            return objectMapper.readValue(jsonStringNotifications, new TypeReference<ArrayList<Notification>>() {
            });
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteAllNotificationCacheByNickname(String nickname) {
        logger.info("Delete all notifications by nickname");
        redisUtil.deleteData(nickname + "Notification");
    }

    @Override
    public void deleteAllNotificationCacheByRoomId(String roomId) {
        logger.info("Delete all notifications by roomId");
        ValueOperations<String, String> allData = redisUtil.getAllData();
        Set<String> keys = allData.getOperations().keys("*");
        if (keys == null) return;
        for (String key : keys) {
            if (!key.contains("Notification")) continue;
            deleteNotificationByRoomIdAndNickname(roomId, key.replace("Notification", ""));
        }
    }

    @Override
    public void deleteNotificationByRoomIdAndNickname(String roomId, String nickname) {
        List<Notification> notifications = findAllNotificationCacheByNickname(nickname);
        Iterator<Notification> iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = iter.next();
            if (notification.getRoomId() != null && roomId.equals(notification.getRoomId())) iter.remove();
        }
        if (notifications.isEmpty()) {
            deleteAllNotificationCacheByNickname(nickname);
        } else {
            saveAllNotificationCache(nickname, notifications);
        }
    }
}
