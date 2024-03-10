package com.pathfind.system.controller;

import com.pathfind.system.findPathService2Dto.FindPathRoom;
import com.pathfind.system.findPathService2Dto.MessageVCRequest;
import com.pathfind.system.service.FindPathRoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/room/*")
public class SendInformationController {

    private static final Logger logger = LoggerFactory.getLogger(SendInformationController.class);

    private final SimpMessagingTemplate template;
    private final FindPathRoomService findPathRoomService;

    /*@MessageMapping(value = "/room/enter")
    public void enter(MessageVCRequest message){
        message.setMessage(message.getSender() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), message);
    }*/

    @MessageMapping(value = "/room/message")
    public void message(MessageVCRequest message) throws IOException {
        FindPathRoom room = findPathRoomService.changeRoomMemberLocation(message.getRoomId(), message.getSender(), message.getMessage());
        message.setMessage(findPathRoomService.findSidewalkShortestRoute(room));
        logger.info("route result: {}", message);
        template.convertAndSend("/sub/service2/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/room/invite")
    public void invite(MessageVCRequest message) throws IOException {
        findPathRoomService.inviteMember(message.getRoomId(), message.getMessage());
    }
}
