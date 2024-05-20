/*
 * 클래스 기능 : 커스텀 로그인 실패 핸들러
 * 최근 수정 일자 : 2024.05.20(월)
 */
package com.pathfind.system.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("Login failed");
        String errorMessage = "";
        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
            errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        }
        else if (exception instanceof OAuth2AuthenticationException) {
            errorMessage = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
        }
        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        //request.setAttribute("err", errorMessage);
        response.sendRedirect("/members/login?error=" + errorMessage);
    }
}
