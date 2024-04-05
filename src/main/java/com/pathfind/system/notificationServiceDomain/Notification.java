/*
 * 클래스 기능 : 알림 정보를 구현한 클래스
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.notificationServiceDomain;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Notification {
    private String id;
    private String content;
    private String url;
    private String roomId;
    private String senderNickname;
    private String receiverNickname;
    private ReadType readType = ReadType.NOT_READ;
    private NotificationType notificationType;

    @Builder(builderMethodName = "innerBuilder")
    private Notification(String content, String url, String roomId, String senderNickname, String receiverNickname, NotificationType notificationType) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.url = url;
        this.roomId = roomId;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.notificationType = notificationType;
    }

    public static NotificationBuilder builder(String content, String senderNickname, String receiverNickname, NotificationType notificationType) {
        return innerBuilder().content(content).senderNickname(senderNickname).receiverNickname(receiverNickname).notificationType(notificationType);
    }

    public void changeReadTypeToREAD() {
        this.readType = ReadType.READ;
    }
}
