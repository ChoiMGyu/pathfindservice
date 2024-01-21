/*
 * 클래스 기능 : 회원 관련 페이지 렌더링을 하는 controller
 * 최근 수정 일자 : 2024.01.20(토)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Check;
import com.pathfind.system.domain.Member;
import com.pathfind.system.dto.EmailRequestDto;
import com.pathfind.system.dto.LoginForm;
import com.pathfind.system.dto.MemberForm;
import com.pathfind.system.dto.PasswordForm;
import com.pathfind.system.service.MemberService;
import com.pathfind.system.validation.ValidationSequence;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    @GetMapping("/updatePassword")
    public String updatePasswordForm(Model model) {
        logger.info("get updatePasswordForm");
        model.addAttribute("passwordForm", new PasswordForm());
        return "members/updatePasswordForm";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@Validated(ValidationSequence.class) PasswordForm form, BindingResult result, Model model, HttpSession session, RedirectAttributes rttr) {
        if(result.hasErrors()) {
            return "members/updatePasswordForm";
        }

        //검증할 패스워드 정규식 패턴
        String regex = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}";
        Pattern pattern = Pattern.compile(regex);

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        logger.info("로그인 멤버의 아이디 : " + loginMember.getId() + ", 패스워드 : " + loginMember.getPassword());

        Matcher matcher = pattern.matcher(form.getNewPassword1());

        if(!form.getOldPassword().equals(loginMember.getPassword())) {
            logger.info("이전 패스워드가 일치하지 않는 경우");
            result.rejectValue("oldPassword", "NotSame.newPassword1");
        }
        if(!form.getNewPassword1().equals(form.getNewPassword2())) {
            logger.info("새 패스워드와 새 패스워드 확인이 일치하지 않는 경우");
            result.rejectValue("newPassword2", "NotSame.newPassword2");
        }
        if(!matcher.matches()) {
            logger.info("새 패스워드 형식이 올바르지 않은 경우");
            result.rejectValue("newPassword2", "Format.newPassword2");
        }

        if(!result.hasErrors()) {
            logger.info("패스워드를 정상적으로 변경했습니다.");
            memberService.updatePassword(loginMember.getId(), form.getOldPassword(), form.getNewPassword1(), form.getNewPassword2());
            rttr.addFlashAttribute("message", "패스워드를 변경했습니다.");
            return "redirect:/members/updatePassword";
        }

        return "members/updatePasswordForm";
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

        //로그인 시도 후 세션을 만들고 휴면 계정 여부 체크
        if(loginMember.getCheck().isDormant()) {
            return "redirect:/members/recover";
        }

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

    @GetMapping("/recover")
    public String recoverUserForm(Model model, HttpSession session) {
        logger.info("get recoverUserForm");

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        //model.addAttribute("recoverForm", new RecoverForm());
        model.addAttribute("userId", loginMember.getUserId());
        logger.info("loginMember userId : " + loginMember.getUserId());

        return "members/recoverUserForm";
    }

    @PostMapping("/recover")
    public String recoverUser(HttpSession session, RedirectAttributes rttr) {
        logger.info("post recoverUser");

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberService.recoverMember(loginMember.getId());
        session.invalidate();
        rttr.addFlashAttribute("message", "휴면 상태를 해제하였습니다.");
        return "redirect:/";
    }

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
    public String validationChk(@Valid MemberForm form, BindingResult result) {
        logger.info("member id check");
        logger.info("error: {}", result);
        Member member = Member.createMember(form.getUserId(), null, form.getNickname(), form.getEmail(), null);
        if (form.getUserId() != null && !memberService.findByUserID(member).isEmpty()) {
            result.addError(new FieldError("memberForm", "userId", "이미 존재하는 아이디입니다"));
        }
        if (form.getNickname() != null && !memberService.findByNickname(member).isEmpty()) {
            result.addError(new FieldError("memberForm", "nickname", "이미 존재하는 닉네임입니다"));
        }
        if (form.getEmail() != null && !memberService.findByEmail(member).isEmpty()) {
            result.addError(new FieldError("memberForm", "email", "이미 존재하는 이메일입니다"));
        }
        logger.info("error: {}", result);
        return "members/registerForm";
    }

    @PostMapping("/register")
    public String postRegister(@Valid MemberForm form, BindingResult result) {
        if(result.hasErrors()) {
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
        if(result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/findUserId";
        }
        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if(memberService.findByEmail(member).isEmpty()) {
            result.addError(new FieldError("emailRequestDto", "email", "존재하지 않는 이메일입니다"));
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

}
