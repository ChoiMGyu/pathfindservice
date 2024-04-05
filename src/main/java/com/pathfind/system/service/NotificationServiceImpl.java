/*
 * 클래스 기능 : 알림 서비스 구현체
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.service;

import com.pathfind.system.notificationServiceDomain.Notification;
import com.pathfind.system.notificationServiceDomain.NotificationType;
import com.pathfind.system.notificationServiceDto.NotificationVCResponse;
import com.pathfind.system.repository.NotificationRepository;
import com.pathfind.system.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 6 * 60 * 60 * 1000L; // millisecond

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final SseEmitterRepository sseEmitterRepository;

    private String makeTimeIncludedId(String nickname) {
        return nickname + "_" + System.currentTimeMillis();
    }

    @Override
    public SseEmitter subscribe(String nickname) {
        logger.info("Notification service subscribe, nickname: {}", nickname);
        String sseEmitterId = makeTimeIncludedId(nickname);
        SseEmitter sseEmitter = sseEmitterRepository.saveSseEmitter(sseEmitterId, new SseEmitter(DEFAULT_TIMEOUT));
        _sendNotification(sseEmitterId, sseEmitter, "EventStream Created. [user nickname = " + nickname + "]"); // SseEmitter 생성 후 메시지를 보내지 않으면 자동으로 접속을 끊기 때문에 더미 메시지를 보내야 한다.
        sendAllNotificationByNickname(sseEmitterId, sseEmitter, nickname); // 모든 알림을 전송해 사용자가 확인할 수 있게 한다.
        return sseEmitter;
    }

    private void _sendNotification(String sseEmitterId, SseEmitter sseEmitter, Object object) {
        Notification notification;
        try {
            notification = (Notification) object;
        } catch (Exception e) { // Notification으로 형변환 불가능하면 더미 메시지이므로 원본 그대로 전송한다.
            try {
                sseEmitter.send(SseEmitter.event().data(object));
            } catch (IOException e2) {
                sseEmitterRepository.deleteEmitterById(sseEmitterId);
            }
            return;
        }
        NotificationVCResponse response = NotificationVCResponse.builder(
                        notification.getContent(),
                        notification.getSenderNickname(),
                        notification.getReceiverNickname(),
                        notification.getReadType(),
                        notification.getNotificationType()
                ) // 회원에게 알림 전송 시 기본적으로 들어가 있어야 하는 정보
                .url(notification.getUrl()) // 초대 알림 전송 시 필요
                .roomId(notification.getRoomId()) // 초대 알림 전송 시 필요
                .build();
        logger.info("_send notification: {}", response);
        try {
            sseEmitter.send(SseEmitter.event().data(response));
        } catch (IOException e) {
            sseEmitterRepository.deleteEmitterById(sseEmitterId);
        }
    }

    @Override
    public void sendInviteNotification(String content, String sender, String receiver, NotificationType notificationType, String url, String roomId) {
        logger.info("Send Notification to {}", receiver);
        Notification notification = Notification.builder(content, sender, receiver, notificationType)
                .url(url)
                .roomId(roomId)
                .build();
        notificationRepository.saveNotificationCache(receiver, notification);
        List<Map.Entry<String, SseEmitter>> sseEmitterEntries = sseEmitterRepository.findAllEmitterByNickname(receiver);
        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitterEntries) {
            _sendNotification(sseEmitterEntry.getKey(), sseEmitterEntry.getValue(), notification);
        }
    }

    @Override
    public void sendAllNotificationByNickname(String sseEmitterId, SseEmitter sseEmitter, String nickname) {
        List<Notification> notifications = notificationRepository.findAllNotificationCacheByNickname(nickname);
        if (notifications.isEmpty()) return;
        logger.info("Send all Notification to {}", nickname);
        for (Notification notification : notifications) {
            _sendNotification(sseEmitterId, sseEmitter, notification);
        }
    }

    @Override
    public void deleteAllNotificationByRoomId(String roomId) {
        logger.info("Delete all Notification by roomId. roomId: {}", roomId);
        notificationRepository.deleteAllNotificationCacheByRoomId(roomId);
    }

    @Override
    public void changeAllReadTypeToReadByNickname(String nickname) {
        List<Notification> notifications = notificationRepository.findAllNotificationCacheByNickname(nickname);
        if (notifications.isEmpty()) return;
        for (Notification notification : notifications) {
            notification.changeReadTypeToREAD();
        }
        notificationRepository.saveAllNotificationCache(nickname, notifications);
    }

    @Override
    public void deleteNotificationByRoomIdAndNickname(String roomId, String nickname) {
        logger.info("Delete Notification by roomId and nickname. roomId: {}, nickname: {}", roomId, nickname);
        notificationRepository.deleteNotificationByRoomIdAndNickname(roomId, nickname);
    }
}
