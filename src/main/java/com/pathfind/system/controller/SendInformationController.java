/*
 * 클래스 기능 : stomp websocket에서 /pub으로 발행된 메시지를 받아 가공하여 같은 방의 인원들에게 전달하는 클래스이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.findPathService2Dto.*;
import com.pathfind.system.service.FindPathRoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class SendInformationController {

    private static final Logger logger = LoggerFactory.getLogger(SendInformationController.class);

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate template;
    private final FindPathRoomService findPathRoomService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) throws IOException {
        logger.info("Connect event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Connect header information: {}", headerAccessor);
        String roomId = headerAccessor.getNativeHeader("roomId").get(0);
        String sender = headerAccessor.getNativeHeader("id").get(0);
        String sessionId = headerAccessor.getSessionId();
        logger.info("roomId: {}, nickname: {}, websocketSessionId: {}", roomId, sender, sessionId);
        FindPathRoom room = findPathRoomService.findRoomById(roomId);
        if (room == null) return;

        MessageVCResponse responseEnterMessage = MessageVCResponse.builder(roomId, MessageType.ENTER)
                .sender(sender)
                .message(sender+"님이 길 찾기 방에 참여하였습니다.")
                .build();
        logger.info("enter message: {}", responseEnterMessage);
        template.convertAndSend("/sub/service2/room/" + roomId, responseEnterMessage);

        FindPathRoom previousRoom = findPathRoomService.memberEnterRoom(roomId, sender, sessionId);
        if (previousRoom == null) return;

        logger.info("{} leaves the room(roomId: {}) because of entering the new room(roomId: {})", sender, previousRoom.getRoomId(), roomId);
        MessageVCResponse responseLeaveMessage = MessageVCResponse.builder(roomId,MessageType.LEAVE)
                .sender(sender)
                .manager(room.getManager().getNickname())
                .message(sender+"님이 길 찾기 방에서 퇴장하였습니다.")
                .build();
        template.convertAndSend("/sub/service2/room/" + previousRoom.getRoomId(), responseLeaveMessage);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) throws IOException {
        logger.info("Subscribe event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Subscribe header information: {}", headerAccessor);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) throws IOException {
        logger.info("Unsubscribe event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Unsubscribe header information: {}", headerAccessor);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws IOException {
        logger.info("Disconnect event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Disconnect header information: {}", headerAccessor);
        String sessionId = headerAccessor.getSessionId();

        FindPathRoom room = findPathRoomService.findRoomByWebSocketSessionId(sessionId);
        if (room == null) return;

        RoomMemberInfo member = room.findMemberByWebSocketSessionId(sessionId);
        logger.info("{} leaves the room: roomId: {}", member.getNickname(), room.getRoomId());
        room = findPathRoomService.leaveRoom(sessionId);
        if (room == null) return;

        MessageVCResponse responseLeaveMessage = MessageVCResponse.builder(room.getRoomId(), MessageType.LEAVE)
                .sender(member.getNickname())
                .manager(room.getManager().getNickname())
                .message(member.getNickname()+"님이 길 찾기 방에서 퇴장하였습니다.")
                .build();
        template.convertAndSend("/sub/service2/room/" + room.getRoomId(), responseLeaveMessage);
    }

    @MessageMapping(value = "/room/message")
    public void message(MessageVCRequest message) throws IOException {
        FindPathRoom room = findPathRoomService.findRoomById(message.getRoomId());

        if (room == null) {
            logger.info("Room deleted because the time assigned for the room has expired. roomId: {}", message.getRoomId());
            MessageVCResponse responseExpiredMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.ROOM_EXPIRED)
                    .message("방에 할당된 두 시간이 만료되어 방이 종료되었습니다.")
                    .build();
            template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseExpiredMessage);
            return;
        }

        if (room.findMemberByNickname(message.getSender()) == null) {
            logger.info("{} leaves room, roomId: {}", message.getSender(), message.getRoomId());
            MessageVCResponse responseLeaveMessage = MessageVCResponse.builder(message.getRoomId(),MessageType.LEAVE)
                    .sender(message.getSender())
                    .manager(room.getManager().getNickname())
                    .message(message.getSender()+"님이 길 찾기 방에서 퇴장하였습니다.")
                    .build();
            template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseLeaveMessage);
            return;
        }

        room = findPathRoomService.changeRoomMemberLocation(message.getRoomId(), message.getSender(), message.getMessage());

        if (message.getSender().equals(room.getManager().getNickname())) {
            if (LocalDateTime.now().isAfter(room.getRoomDeletionTime())) {
                logger.info("Room deleted because no one came to the room for 5 minutes. roomId: {}", message.getRoomId());
                MessageVCResponse responseExpiredMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.ROOM_EXPIRED)
                        .message("5분간 아무도 들어오지 않아 방이 종료되었습니다.")
                        .build();
                template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseExpiredMessage);
                return;
            }
        } else if (LocalDateTime.now().isAfter(room.findMemberByNickname(message.getSender()).getRoomExitTime())) {
            logger.info("{} leaves room because he doesn't move for 10 minutes, roomId: {}", message.getSender(), message.getRoomId());
            MessageVCResponse responseLeaveMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.LEAVE)
                    .sender(message.getSender())
                    .manager(room.getManager().getNickname())
                    .message(message.getSender()+"님이 10분간 움직이지 않아 방에서 퇴장되었습니다.")
                    .build();
            template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseLeaveMessage);
            return;
        }
        MessageVCResponse responseRouteMessage;
        if (room.findMemberByNickname(message.getSender()).getIsRoad()) {
             responseRouteMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.ROUTE)
                     .sender(message.getSender())
                     .route(findPathRoomService.findRoadShortestRoute(room))
                     .build();
        } else {
            responseRouteMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.ROUTE)
                    .sender(message.getSender())
                    .route(findPathRoomService.findSidewalkShortestRoute(room))
                    .build();
        }
        logger.info("route result: {}", message);
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseRouteMessage);
    }

    @MessageMapping(value = "/room/delete")
    public void deleteRoom(MessageVCRequest message) throws IOException {
        logger.info("{} delete the room, roomId: {}", message.getSender(), message.getRoomId());
        FindPathRoom room = findPathRoomService.findRoomById(message.getRoomId());
        //FindPathRoom room = findPathRoomService.leaveRoom(message.getRoomId(), message.getSender());
        if (room == null) return;
        MessageVCResponse responseExpiredMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.ROOM_EXPIRED)
                .message(message.getSender() + "님이 길 찾기 방을 삭제하였습니다.")
                .build();
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseExpiredMessage);
        findPathRoomService.deleteRoom(message.getRoomId());
    }

    @MessageMapping(value = "/room/sendMessage")
    public void sendMessage(MessageVCRequest message) throws IOException {
        logger.info("{} send message to the room, roomId: {}", message.getSender(), message.getRoomId());
        FindPathRoom room = findPathRoomService.findRoomById(message.getRoomId());
        if (room == null) return;
        MessageVCResponse responseNotInCampusMessage = MessageVCResponse.builder(message.getRoomId(), MessageType.NOT_IN_CAMPUS)
                .sender(message.getSender())
                .message(message.getMessage())
                .build();
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), responseNotInCampusMessage);
    }

    /*@MessageMapping(value = "/room/enter")
    public void enter(MessageVCRequest message) throws IOException {
        logger.info("enter room, roomId: {}", message.getRoomId());
        FindPathRoom room = findPathRoomService.findRoomById(message.getRoomId());
        if (room.findMemberByNickname(message.getSender()).getIsInRoom()) return;

        MessageInfoVCResponse responseEnterMessage = new MessageInfoVCResponse();
        responseEnterMessage.setMessage(message.getSender() + "님이 길 찾기 방에 참여하였습니다.");
        responseEnterMessage.setManager(room.getManager().getNickname());
        message.setMessage(objectMapper.writeValueAsString(responseEnterMessage));
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), message);
        FindPathRoom previousRoom = findPathRoomService.memberEnterRoom(message.getRoomId(), message.getSender());
        if (previousRoom == null) return;

        logger.info("{} leaves the room(roomId: {}) because of entering the new room(roomId: {})", message.getSender(), previousRoom.getRoomId(), message.getRoomId());
        MessageInfoVCResponse responseLeaveMessage = new MessageInfoVCResponse();
        responseLeaveMessage.setManager(previousRoom.getManager().getNickname());
        responseLeaveMessage.setLeave(true);
        responseLeaveMessage.setMessage(message.getSender() + "님이 길 찾기 방에서 퇴장하였습니다.");
        message.setMessage(objectMapper.writeValueAsString(responseLeaveMessage));
        template.convertAndSend("/sub/service2/room/" + previousRoom.getRoomId(), message);
    }*/

    /*@MessageMapping(value = "/room/leave")
    public void leave(MessageVCRequest message) throws IOException {
        logger.info("{} leaves the room, roomId: {}", message.getSender(), message.getRoomId());
        FindPathRoom room = findPathRoomService.leaveRoom(message.getRoomId(), message.getSender());
        if (room == null) return;
        MessageInfoVCResponse responseMessage = new MessageInfoVCResponse();
        responseMessage.setManager(room.getManager().getNickname());
        responseMessage.setLeave(true);
        responseMessage.setMessage(message.getSender() + "님이 길 찾기 방에서 퇴장하였습니다.");
        message.setMessage(objectMapper.writeValueAsString(responseMessage));
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), message);
    }*/
}
