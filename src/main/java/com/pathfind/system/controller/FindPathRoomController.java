package com.pathfind.system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.domain.Member;
import com.pathfind.system.findPathService2Dto.FindPathRoom;
import com.pathfind.system.service.FindPathRoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/service2/*")
public class FindPathRoomController {

    private static final Logger logger = LoggerFactory.getLogger(FindPathRoomController.class);

    private final FindPathRoomService findPathRoomService;

    // service2 메인 화면
    @GetMapping("/enter")
    public String enterService2(HttpServletRequest request, Model model) {
        logger.info("enter service2 defaultPage");
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, null);

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        //logger.info("loginMember: {}", loginMember.getNickname());

        return "/service2/service2Home";
    }

    //채팅방 개설
    @PostMapping(value = "/create-room")
    public String createRoom(@ModelAttribute(value = "name") String name, HttpServletRequest request) throws JsonProcessingException {
        logger.info("create service2 room , name: {}", name);
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        FindPathRoom newRoom = findPathRoomService.createRoom(loginMember.getNickname(), name);
        return "redirect:/service2/room?roomId="+newRoom.getRoomId();
    }

    //채팅방 조회
    @GetMapping("/room")
    public void getRoom(@RequestParam(value = "roomId") String roomId, Model model, HttpServletRequest request) throws IOException {
        logger.info("get service2 room, roomId : {}", roomId);
        //logger.info("room: {}", findPathRoomService.findRoomById(roomId));
        HttpSession session = request.getSession();
        model.addAttribute("room", findPathRoomService.findRoomById(roomId));
    }
}
