package com.pathfind.system.service;

import com.pathfind.system.findPathDto.ShortestPathRoute;
import com.pathfind.system.findPathService2Dto.MessageType;
import com.pathfind.system.findPathService2Dto.MessageVCResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SendStompMessageServiceImpl implements SendStompMessageService {

    private final SimpMessagingTemplate template;

    @Override
    public void sendEnter(String roomId, String sender, String message, int curMemberNum) {
        MessageVCResponse response = MessageVCResponse.builder(roomId, MessageType.ENTER)
                .sender(sender)
                .message(message)
                .curMemberNum(curMemberNum)
                .build();
        template.convertAndSend("/sub/service2/room/" + roomId, response);
    }

    @Override
    public void sendLeave(String roomId, String sender, String manager, String message, int curMemberNum) {
        MessageVCResponse response = MessageVCResponse.builder(roomId, MessageType.LEAVE)
                .sender(sender)
                .manager(manager)
                .message(message)
                .curMemberNum(curMemberNum)
                .build();
        template.convertAndSend("/sub/service2/room/" + roomId, response);
    }

    @Override
    public void sendExpired(String roomId, String message) {
        MessageVCResponse response = MessageVCResponse.builder(roomId, MessageType.ROOM_EXPIRED)
                .message(message)
                .build();
        template.convertAndSend("/sub/service2/room/" + roomId, response);
    }

    @Override
    public void sendRoute(String roomId, String sender, List<List<ShortestPathRoute>> route) {
        MessageVCResponse response = MessageVCResponse.builder(roomId, MessageType.ROUTE)
                .sender(sender)
                .route(route)
                .build();
        template.convertAndSend("/sub/service2/room/" + roomId, response);
    }

    @Override
    public void sendNotInCampus(String roomId, String sender, String message) {
        MessageVCResponse response = MessageVCResponse.builder(roomId, MessageType.NOT_IN_CAMPUS)
                .sender(sender)
                .message(message)
                .build();
        template.convertAndSend("/sub/service2/room/" + roomId, response);
    }
}
