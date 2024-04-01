/*
 * 클래스 기능 : stomp websocket에서 /pub으로 발행된 메시지를 받아 가공하여 같은 방의 인원들에게 전달하는 클래스이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.findPathService2Domain.FindPathRoom;
import com.pathfind.system.findPathService2Domain.MemberLatLng;
import com.pathfind.system.findPathService2Domain.RoomMemberType;
import com.pathfind.system.findPathService2Domain.TransportationType;
import com.pathfind.system.findPathService2Dto.*;
import com.pathfind.system.service.FindPathRoomService;
import com.pathfind.system.service.SendStompMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;


@Controller
@RequiredArgsConstructor
public class SendInformationController {

    private static final Logger logger = LoggerFactory.getLogger(SendInformationController.class);

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate template;
    private final FindPathRoomService findPathRoomService;
    private final SendStompMessageService sendStompMessageService;

    @EventListener
    public void sendLeaveMessage(SessionConnectEvent event) throws IOException {
        //연결이 이루어질 때 SessionConnect 메시지를 서버에 전송 -> SessionConnected 메시지를 서버로부터 전달받음
        //즉, 의도한 동작은 SessionConnect 메시지를 보낼 때 이전 방을 찾고 그 방에서 퇴장하고
        //SessionConnected 메시지를 받을 때 새로운 방에 입장을 한다
        logger.info("Connect message를 서버로 전송");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender = (String) headerAccessor.getNativeHeader("sender").get(0);
        //sender가 누군지 확인하고
        FindPathRoom beforeHasRoom = findPathRoomService.leaveRoom(sender);
        //이전 방이 있었는지 확인
        if(beforeHasRoom != null) {
            //방이 있었으므로 방을 나옴
            findPathRoomService.leaveRoom(sender);
        }
        sendStompMessageService.sendLeave(beforeHasRoom.getRoomId(), sender, beforeHasRoom.getOwnerName(), sender + "님이 길 찾기 방에서 퇴장하였습니다.");
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws IOException {
        logger.info("Connected message를 서버로부터 전달받음");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Connect header information: {}", headerAccessor);
        String roomId = Objects.requireNonNull(headerAccessor.getNativeHeader("roomId")).get(0);
        String sender = Objects.requireNonNull(headerAccessor.getNativeHeader("sender")).get(0);
        // sender headerName id에서 sender로 수정하였음
        // stomp Connect 메시지 헤더에 roomId와 id 값이 포함되어 있지 않으면 개발자가 의도한 것이 아니므로 함수를 종료한다.
        if (roomId == null || sender == null) return;

        headerAccessor.getSessionAttributes().put("roomId", roomId);
        headerAccessor.getSessionAttributes().put("sender", sender);

        sendStompMessageService.sendEnter(roomId, sender, sender + "님이 길 찾기 방에 참여하였습니다.");
        findPathRoomService.memberEnterRoom(roomId, sender, RoomMemberType.OWNER, TransportationType.ROAD);
        //memberEnterRoom에서 OWNER와 ROAD는 다시 확인해야함
        //RoomMemberType의 경우 그 방에 방장이 존재하는지 존재하지 않는지에 따라 다르며
        //TransportationType의 경우 그 방이 어떤 교통수단을 이용하는지에 따라 다르다
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws IOException {
        logger.info("Disconnect message를 서버로부터 전달 받음");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("Disconnect header information: {}", headerAccessor);

        String sender = headerAccessor.getSessionAttributes().get("sender").toString();
        String roomId = headerAccessor.getSessionAttributes().get("roomId").toString();
        //Disconnect 유저(sender)와 방(roomId)을 전달받고

        FindPathRoom curRoom = findPathRoomService.findRoomById(roomId);
        //나갈 방을 찾은 후
        curRoom.leaveRoomCurMember(sender);
        //sender는 방을 나가고 추가적인 동작(방장을 업데이트 또는 방을 없앰)을 한다
        if(curRoom.getCurMember().isEmpty()) {
            logger.info("{}의 퇴장으로 방에 아무도 남지 않았으므로 방을 삭제합니다.", sender);
            findPathRoomService.deleteRoom(roomId);
            sendStompMessageService.sendExpired(roomId, sender + "님이 길 찾기 방을 삭제하였습니다.");
            //sendExpired 메시지는 동작상 UI를 통해 확인할 수 없는 부분이지만 필요한 메시지 전송이므로 넣어둠
            return;
        }
        //sender가 방을 나가면서 방에 아무도 남지 않는 경우 방을 삭제한다

        logger.info("방 인원들에게 퇴장 메시지를 전송합니다.");
        sendStompMessageService.sendLeave(curRoom.getRoomId(), sender, curRoom.getOwnerName(), sender + "님이 길 찾기 방에서 퇴장하였습니다.");
        //sender가 나가도 방에 인원이 남아있으므로 퇴장 메시지를 전송한다
    }

    @MessageMapping(value = "/room/route")
    public void getRoute(MessageVCRequest message) throws IOException {
        //고친 부분 없음
        FindPathRoom room = findPathRoomService.findRoomById(message.getRoomId());

        if (room == null) {
            logger.info("Room deleted because the time assigned for the room has expired. roomId: {}", message.getRoomId());
            sendStompMessageService.sendExpired(message.getRoomId(), "방에 할당된 두 시간이 만료되어 방이 종료되었습니다.");
            return;
        }

        if (room.findMemberByNickname(message.getSender()) == null) {
            logger.info("{} leaves room, roomId: {}", message.getSender(), message.getRoomId());
            sendStompMessageService.sendLeave(message.getRoomId(), message.getSender(), room.getManagerNickname(), message.getSender() + "님이 길 찾기 방에서 퇴장하였습니다.");
            return;
        }

        room = findPathRoomService.changeRoomMemberLocation(message.getRoomId(), message.getSender(), objectMapper.readValue(message.getMessage(), MemberLatLng.class));

        if (message.getSender().equals(room.getManagerNickname())) {
            if (LocalDateTime.now().isAfter(room.getRoomDeletionTime())) {
                logger.info("Room deleted because no one came to the room for 5 minutes. roomId: {}", message.getRoomId());
                sendStompMessageService.sendExpired(message.getRoomId(), "5분간 아무도 들어오지 않아 방이 종료되었습니다.");
                return;
            }
        } else if (LocalDateTime.now().isAfter(room.findMemberByNickname(message.getSender()).getRoomExitTime())) {
            logger.info("{} leaves room because he doesn't move for 10 minutes, roomId: {}", message.getSender(), message.getRoomId());
            sendStompMessageService.sendLeave(message.getRoomId(), message.getSender(), room.getManagerNickname(), message.getSender() + "님이 10분간 움직이지 않아 방에서 퇴장되었습니다.");
            return;
        }

        if (room.findMemberByNickname(message.getSender()).getTransportationType() == TransportationType.ROAD) {
            sendStompMessageService.sendRoute(message.getRoomId(), message.getSender(), findPathRoomService.findRoadShortestRoute(room));
        } else {
            sendStompMessageService.sendRoute(message.getRoomId(), message.getSender(), findPathRoomService.findSidewalkShortestRoute(room));
        }
        // logger.info("route result: {}", message);
    }

    @MessageMapping(value = "/room/delete")
    public void deleteRoom(MessageVCRequest message) {
        logger.info("{}가 {} 방을 삭제하였음.", message.getSender(), message.getRoomId());

        //올바른 상황에서 호출된다고 하면 이전의 작성된 로직은 필요가 없다고 생각하였음
        //room == null이어서 return이 되는 상황은 애초에 "/room/delete"가 호출되면 안되는 상황임
        sendStompMessageService.sendExpired(message.getRoomId(), message.getSender() + "님이 길 찾기 방을 삭제하였습니다.");
        findPathRoomService.deleteRoom(message.getRoomId());
    }

    @MessageMapping(value = "/room/out-campus")
    public void sendNotInCampus(MessageVCRequest message) {
        logger.info("{} send message to the room, roomId: {}", message.getSender(), message.getRoomId());

        //위와 마찬가지의 이유로 "/room/out-campus"가 올바른 상황에 만들어지면 room == null일 경우가 없음
        sendStompMessageService.sendNotInCampus(message.getRoomId(), message.getSender(), message.getMessage());
    }
}
