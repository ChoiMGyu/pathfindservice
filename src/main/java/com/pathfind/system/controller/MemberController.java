/*
 * 클래스 기능 : 회원 관련 페이지 렌더링을 하는 controller
 * 최근 수정 일자 : 2024.01.15(월)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Member;
import com.pathfind.system.dto.*;
import com.pathfind.system.service.MemberService;
import jakarta.mail.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.pathfind.system.domain.Check;
import com.pathfind.system.domain.Member;
import com.pathfind.system.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    @GetMapping(value = "/new")
    public String getRegister(Model model) {
        logger.info("get register");
        model.addAttribute("memberForm", new MemberForm());

        return "members/registerForm";
    }

    @GetMapping(value = "/agree")
    public String getAgree() {
        logger.info("get register");
        return "members/agree";
    }

    @PostMapping(value = "/validationChk")
    public String validationChk(@Valid MemberForm form, BindingResult result, Model model) {
        logger.info("member id check");
        logger.info("error: {}", result);
        Member member = Member.createMember(form.getUserId(), null, form.getNickname(), form.getEmail(), null);
        if (form.getUserId() != null && !memberService.findByUserID(member).isEmpty()) {
            result.rejectValue("userId", "UserId.exist");
        }
        if (form.getNickname() != null && !memberService.findByNickname(member).isEmpty()) {
            result.rejectValue("nickname", "Nickname.exist");
        }
        if (form.getEmail() != null && !memberService.findByEmail(member).isEmpty()) {
            result.rejectValue("email", "Email.exist");
        }
        if(result.hasErrors()) return "members/registerForm";

        model.addAttribute("message", "아이디, 닉네임, 이메일 중복 확인을 통과하였습니다.");

        logger.info("error: {}", result);
        return "members/registerForm";
    }

    @PostMapping("/register")
    public String postRegister(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/registerForm";
        }
        Check check = Check.createCheck();
        check.changeEmailAuth(true);
        check.changeInformationAgree(true);
        Member newMember = Member.createMember(form.getUserId(), form.getPassword(), form.getNickname(), form.getEmail(), check);
        memberService.register(newMember);

        return "members/registerComplete";
    }

    @GetMapping("/findUserId")
    public String findUserId(Model model) {
        logger.info("find userId");
        model.addAttribute("emailRequestDto", new EmailRequestDto());

        return "members/findUserId";
    }

    @PostMapping("/isValidEmail")
    public String isValidEmail(@Valid EmailRequestDto form, BindingResult result) {
        if (result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/findUserId";
        }
        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if (memberService.findByEmail(member).isEmpty()) {
            result.rejectValue("email", "Email.notExist");
        }
        logger.info("email validation error: {}", result);

        return "members/findUserId";
    }

    @PostMapping("/returnId")
    public String returnId(EmailRequestDto form, Model model) {
        String userId = memberService.findUserIdByEmail(form.getEmail());
        model.addAttribute("userId", userId);

        return "members/yourUserId";
    }

    @GetMapping("/findPassword")
    public String findPassword(Model model) {
        logger.info("find password");
        model.addAttribute("findPasswordForm", new FindPasswordForm());

        return "members/findPassword";
    }

    @PostMapping("/isValidIdEmail")
    public String isValidIdEmail(@Valid FindPasswordForm form, BindingResult result) {
        if (result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/findPassword";
        }
        try {
            memberService.idEmailChk(form.getUserId(), form.getEmail());
        } catch (Exception e) {
            result.reject("userInfo", e.getMessage());
        }

        return "members/findPassword";
    }

    @PostMapping("/returnPassword")
    public String returnPassword(FindPasswordForm form) {
        memberService.findPassword(form.getUserId(), form.getEmail());

        return "members/yourPassword";
    }

    @GetMapping("/updatePassword")
    public String updatePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "members/updatePasswordForm";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@Valid PasswordForm form, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "members/updatePasswordForm";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
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
        if (result.hasErrors()) {
            return "members/loginForm";
        }
        Member loginMember = memberService.login(form.getUserId(), form.getPassword());
        logger.info("login? : {}", loginMember);

        if (loginMember == null) {
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
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/myProfile")
    public String getProfile(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        MemberForm memberForm = new MemberForm();
        memberForm.setUserId(member.getUserId());
        memberForm.setEmail(member.getEmail());
        NicknameForm nicknameForm = new NicknameForm();
        nicknameForm.setNickname(member.getNickname());

        model.addAttribute("memberForm", memberForm);
        model.addAttribute("nicknameForm", nicknameForm);

        return "members/myProfile";
    }

    @PostMapping("/updateNickname")
    public String updateNickname(MemberForm memberForm, @Valid NicknameForm nicknameForm, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberForm.setUserId(member.getUserId());
        memberForm.setEmail(member.getEmail());
        if (result.hasErrors()) {
            return "/members/myProfile";
        }

        Member newMember = memberService.updateNickname(member.getId(), nicknameForm.getNickname());
        if(newMember == null) {
            result.rejectValue("nickname", "Nickname.exist");
            return "/members/myProfile";
        }

        session.setAttribute(SessionConst.LOGIN_MEMBER, newMember);
        rttr.addFlashAttribute("message", "닉네임을 변경했습니다.");

        return "redirect:/members/myProfile";
    }

    @GetMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        model.addAttribute("emailRequestDto", new EmailRequestDto());

        return "members/updateEmail";
    }

    @PostMapping("/isValidEmail2")
    public String isValidEmail2(@Valid EmailRequestDto form, BindingResult result) {
        if (result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/updateEmail";
        }

        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if (!memberService.findByEmail(member).isEmpty()) {
            result.rejectValue("email", "Email.exist");
            return "members/updateEmail";
        }

        return "members/updateEmail";
    }

    @PostMapping("/updateEmail")
    public String updateEmail(@Valid EmailRequestDto form, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        member = memberService.updateEmail(member.getId(), form.getEmail());
        rttr.addFlashAttribute("message", "닉네임을 변경했습니다.");
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        return "redirect:/members/myProfile";
    }

    @GetMapping("/leave")
    public String leaveNotice(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        return "members/leaveNotice";
    }

    @PostMapping("/leave")
    public String leaveService(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberService.deleteMember(member.getId());
        session.setAttribute(SessionConst.LOGIN_MEMBER, null);

        return "members/bye";
    }
}
