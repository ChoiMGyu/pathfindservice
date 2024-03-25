/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2)의 페이지들을 렌더링하는 클래스이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pathfind.system.domain.Member;
import com.pathfind.system.findPathService2Dto.CreateRoomVCRequest;
import com.pathfind.system.findPathService2Dto.FindPathRoom;
import com.pathfind.system.findPathService2Dto.InviteMemberVCRequest;
import com.pathfind.system.findPathService2Dto.InviteMemberVCResponse;
import com.pathfind.system.service.FindPathRoomService;
import com.pathfind.system.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/service2/*")
public class FindPathRoomController {

    private static final Logger logger = LoggerFactory.getLogger(FindPathRoomController.class);

    private final FindPathRoomService findPathRoomService;
    private final MemberService memberService;

    // service2 메인 화면
    @GetMapping("/enter")
    public String enterService2(HttpServletRequest request, Model model, RedirectAttributes rttr) {
        logger.info("Enter service2 defaultPage");
        HttpSession session = request.getSession(false);
        if (session == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, null);

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }

        model.addAttribute("CreateRoomVCRequest", new CreateRoomVCRequest());

        //logger.info("loginMember: {}", loginMember.getNickname());

        return "service2/service2Home";
    }

    //채팅방 개설
    @PostMapping(value = "/create-room")
    public String createRoom(@ModelAttribute(value = "CreateRoomVCRequest") CreateRoomVCRequest form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) throws JsonProcessingException {
        logger.info("Create service2 room , name: {}", form.getRoomName());
        HttpSession session = request.getSession(false);
        if (session == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }

        String patternUserId = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_\\s]{2,30}$";
        boolean regexUserId = Pattern.matches(patternUserId, form.getRoomName());
        if (!regexUserId) {
            result.rejectValue("roomName", "Format.roomName");
            return "service2/service2Home";
        }

        FindPathRoom newRoom = findPathRoomService.createRoom(loginMember.getNickname(), form.getRoomName(), form.getTransportation().equals("도로"));
        return "redirect:/service2/room?roomId=" + newRoom.getRoomId();
    }

    //채팅방 조회
    @GetMapping("/room")
    public String getRoom(@RequestParam(value = "roomId") String roomId, Model model, HttpServletRequest request, RedirectAttributes rttr) throws IOException {
        logger.info("get service2 room, roomId : {}", roomId);
        //logger.info("room: {}", findPathRoomService.findRoomById(roomId));
        HttpSession session = request.getSession(false);
        if (session == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, null);

        //세션에 회원 데이터가 없으면 redirect:/
        if (loginMember == null) {
            rttr.addFlashAttribute("message", "사람간 길찾기 서비스 이용을 하시려면 로그인을 진행해 주세요.");
            return "redirect:/";
        }

        FindPathRoom room = findPathRoomService.findRoomById(roomId);

        // roomId에 해당하는 방이 없다면 redirect:/
        if (room == null) {
            rttr.addFlashAttribute("message", "방 아이디 '" + roomId + "'에 해당하는 방이 존재하지 않습니다.");
            return "redirect:/";
        }

        //길찾기 방에 회원에 초대받지 않았다면 redirect:/
        if (room.findMemberByNickname(loginMember.getNickname()) == null) {
            rttr.addFlashAttribute("message", "방 이름 '" + room.getRoomName() + "'에 해당하는 방에 초대받지 못했습니다.");
            return "redirect:/";
        }

        //findPathRoomService.memberEnterRoom(roomId, loginMember.getNickname());

        model.addAttribute("room", room);
        model.addAttribute("inviteMemberVCRequest", new InviteMemberVCRequest());

        return "service2/room";
    }

    // 채팅방 초대
    @ResponseBody
    @GetMapping("/room/invite")
    public InviteMemberVCResponse invite(@Valid InviteMemberVCRequest form) throws IOException {
        String roomId = form.getRoomId(), nickname = form.getNickname();
        logger.info("Invite {} at room, roomId: {}", nickname, roomId);
        Member member = Member.createMember(null, null, nickname, null, null);
        InviteMemberVCResponse response = new InviteMemberVCResponse();

        // nickname이 데이터베이스에 존재하는 것인지 여부를 확인한다.
        if (memberService.findByNickname(member).isEmpty()) {
            logger.info("{} isn't exist at DB", nickname);
            response.setInvited(false);
            response.setMessage("'" + nickname + "'은 존재하지 않는 닉네임 입니다.");
            return response;
        }

        // 이미 roomId에 해당하는 길찾기 방에 초대가 되어 있는지 여부를 확인한다.
        if (findPathRoomService.isMemberInRoom(roomId, nickname)) {
            logger.info("{} is already invited at room, roomId {}", roomId, nickname);
            response.setInvited(false);
            response.setMessage("'" + nickname + "'님은 이미 초대되었습니다.");
            return response;
        }

        FindPathRoom room = findPathRoomService.inviteMember(roomId, nickname);
        response.setMessage("'" + nickname + "'님이 " + room.getRoomName() + "방에 초대되었습니다.");
        logger.info("{} is invited at room successfully, roomId: {}", nickname, roomId);

        return response;
    }

    // 채팅방 퇴장
    @GetMapping("/room/leave")
    public String leaveRoom(@RequestParam(value = "roomName") String roomName, @RequestParam(value = "reason") String reason, RedirectAttributes rttr) {
        logger.info("Leave room, roomName: {}", roomName);
        rttr.addFlashAttribute("message", reason);
        return "redirect:/";
    }
}
