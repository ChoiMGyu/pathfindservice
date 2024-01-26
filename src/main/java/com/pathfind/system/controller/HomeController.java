/*
 * 클래스 기능 : 로그인 여부에 따라 다른 홈화면을 제공하는 controller
 * 최근 수정 일자 : 2024.01.20(토)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Member;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @GetMapping("/")
    public String homeLogin(HttpServletRequest request, Model model) {
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

/*        logger.info("loginMember.getCheck().isDormant: {}", loginMember.getId());
        logger.info("loginMember.getCheck().isDormant: {}", loginMember.getCheck().isDormant());*/
        //휴면 계정이 홈화면으로 이동시에는 세션을 무효화하고 이동
        if(loginMember.getCheck().isDormant()) {
            session.invalidate();
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
