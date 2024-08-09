/*
 * 클래스 기능 : 커스텀 로그아웃 성공 핸들러
 * 최근 수정 일자 : 2024.08.08(목)
 */
package com.pathfind.system.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfind.system.memberDto.LogoutFailureVCResponse;
import com.pathfind.system.service.CookieServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CookieServiceImpl cookieService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("trying logout...");
        String accessToken = cookieService.findAccessToken(request);
        String refreshToken = cookieService.findRefreshToken(request);

        String message = "";
        if (accessToken == null || refreshToken == null) {
            logger.info("fail to logout");
            message = "로그아웃에 실패했습니다.";
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            LogoutFailureVCResponse logoutFailureVCResponse = new LogoutFailureVCResponse(message);
            String result = objectMapper.writeValueAsString(logoutFailureVCResponse);
            response.getWriter().print(result);
            return;
        }

        logger.info("logout success");
        response.sendRedirect("/");
    }
}
