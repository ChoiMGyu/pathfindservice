/*
 * 클래스 기능 : 회원 관련 페이지 렌더링을 하는 controller
 * 최근 수정 일자 : 2024.01.15(월)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Member;
import com.pathfind.system.dto.LoginForm;
import com.pathfind.system.dto.PasswordForm;
import com.pathfind.system.service.MemberService;
import jakarta.mail.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    @GetMapping("/updatePassword")
    public String updatePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "members/updatePasswordForm";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@Valid PasswordForm form, BindingResult result, HttpSession session) {
        if(result.hasErrors()) {
            return "members/updatePasswordForm";
        }

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        logger.info("로그인 멤버의 아이디 : " + loginMember.getId() + ", 패스워드 : " + loginMember.getPassword());

        memberService.updatePassword(loginMember.getId(), form.getOldPassword(), form.getNewPassword1(), form.getNewPassword2());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        logger.info("get login");
        model.addAttribute("loginForm", new LoginForm());
        return "members/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult result, HttpServletRequest request) {
        logger.info("post login");
        if(result.hasErrors()) {
            return "members/loginForm";
        }
        Member loginMember = memberService.login(form.getUserId(), form.getPassword());
        logger.info("login? : {}", loginMember);

        if(loginMember == null) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            <div th:if="${#fields.hasGlobalErrors()}">
//            <p class="field-error" th:each="err : ${#fields.globalErrors()}"
//            th:text="${err}">전체 오류 메시지</p></div>
            return "members/loginForm";
        }
        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 보관 (문자열 상수로 세션 ID 재활용 "loginMember")
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
