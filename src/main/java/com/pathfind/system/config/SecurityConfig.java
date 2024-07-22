/*
 * 클래스 기능 : Spring security config class
 * 최근 수정 일자 : 2024.07.22(월)
 */
package com.pathfind.system.config;

import com.pathfind.system.handler.CustomAuthenticationFailureHandler;
import com.pathfind.system.handler.CustomAuthenticationSuccessHandler;
import com.pathfind.system.service.PrincipalOAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터(SecurityConfig class)가 스프링 필터 체인에 등록이 된다.
@EnableMethodSecurity(securedEnabled = true) // secured 애노테이션 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOAuth2UserServiceImpl principalOAuth2UserService;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable) //개발 단계에서는 주석해제, 배포에서는 주석처리
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/loginForm")
                        .usernameParameter("userId")
                        .loginProcessingUrl("/members/login")
                        .successHandler(customAuthenticationSuccessHandler) // 커스텀 로그인 성공 핸들러 설정
                        .failureHandler(customAuthenticationFailureHandler) // 커스텀 로그인 실패 핸들러 설정
                )
                .oauth2Login(formLogin -> formLogin
                        .loginPage("/members/loginForm")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOAuth2UserService))
                        .successHandler(customAuthenticationSuccessHandler) // 커스텀 로그인 성공 핸들러 설정
                        .failureHandler(customAuthenticationFailureHandler) // 커스텀 로그인 실패 핸들러 설정
                )
                .logout(formLogout -> formLogout
                        .logoutUrl("/members/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1) //동일한 아이디로 동시에 허용되는 세션의 최대 개수
                        .maxSessionsPreventsLogin(false)); //최대 세션 개수를 초과할 경우, 이전에 로그인한 세션이 로그아웃되고 새로운 세션이 활성화

        http
                .sessionManagement((auth) -> auth //세션 고정 공격을 방지화
                        .sessionFixation().changeSessionId()); //사용자가 로그인할 때 기존 세션 ID를 새로운 세션 ID로 변경


        return http.build();
    }
}
