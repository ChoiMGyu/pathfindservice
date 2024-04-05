/*
 * 클래스 기능 : 알림 기능에 관한 컨트롤러. 사용자가 알림을 구독하거나 확인할 때 사용된다.
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.controller;

import com.pathfind.system.service.NotificationService;
import com.pathfind.system.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe")
    public SseEmitter subscribeNotification(@RequestParam(value = "nickname") String nickname) {
        logger.info("Notification subscribe. id: {}", nickname);
        return notificationService.subscribe(nickname);
    }

    @GetMapping(value = "/check")
    public void checkAllNotification(@RequestParam(value = "nickname") String nickname) {
        logger.info("Change all notifications' read type to READ. id: {}", nickname);
        notificationService.changeAllReadTypeToReadByNickname(nickname);
    }
}
