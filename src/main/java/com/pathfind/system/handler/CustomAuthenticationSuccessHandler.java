/*
 * 클래스 기능 : 커스텀 로그인 성공 핸들러
 * 최근 수정 일자 : 2024.05.19(일)
 */
package com.pathfind.system.handler;

import com.pathfind.system.authDto.PrincipalDetails;
import com.pathfind.system.controller.SessionConst;
import com.pathfind.system.domain.Member;
import com.pathfind.system.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Login success");
        Member member = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        logger.info("userId: {}", member.getUserId());
        memberService.updateLastConnect(member.getUserId());
        Member loginMember = Member.createMember(member.getUserId(), null, member.getNickname(), member.getEmail(), null);
        HttpSession session = request.getSession();
        loginMember.changeId(member.getId());
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        response.sendRedirect("/");
    }
}
