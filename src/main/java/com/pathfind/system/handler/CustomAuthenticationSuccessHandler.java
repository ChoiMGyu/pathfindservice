/*
 * 클래스 기능 : 커스텀 로그인 성공 핸들러
 * 최근 수정 일자 : 2024.08.08(목)
 */
package com.pathfind.system.handler;

import com.pathfind.system.authDto.PrincipalDetails;
import com.pathfind.system.domain.Member;
import com.pathfind.system.jwtDto.IssuedTokenCSResponse;
import com.pathfind.system.service.CookieServiceImpl;
import com.pathfind.system.service.JwtServiceImpl;
import com.pathfind.system.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;

    private final JwtServiceImpl jwtService;

    private final CookieServiceImpl cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Login success");
        Member member = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        logger.info("userId: {}", member.getUserId());
        memberService.updateLastConnect(member.getUserId());
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            for (Cookie cookie : request.getCookies()) {
                String name = cookie.getName();
                if (name.equals("JSESSIONID")) {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        IssuedTokenCSResponse token = jwtService.createToken(member.getUserId());
        cookieService.addAccessTokenCookie(response, token.getAccessToken());
        cookieService.addRefreshTokenCookie(response, token.getRefreshToken());

        if (member.getUserId().contains("_")) response.sendRedirect("/");
    }
}
