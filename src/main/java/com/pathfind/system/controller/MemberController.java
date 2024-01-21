/*
 * 클래스 기능 : 회원 관련 페이지 렌더링을 하는 controller
 * 최근 수정 일자 : 2024.01.15(월)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Member;
import com.pathfind.system.dto.*;
import com.pathfind.system.service.MailSendService;
import com.pathfind.system.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.pathfind.system.domain.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    private final MailSendService mailSendService;

    @GetMapping(value = "/agree")
    public String getAgree(HttpServletRequest request) {
        logger.info("get register");
        HttpSession session = request.getSession();
        session.invalidate();
        return "members/agree";
    }

    @GetMapping(value = "/new")
    public String getRegister(HttpServletRequest request, Model model) {
        logger.info("get register");
        HttpSession session = request.getSession();
        RegisterForm registerForm = (RegisterForm) session.getAttribute(SessionConst.REGISTER_MEMBER);
        if (registerForm == null) {
            session.setAttribute(SessionConst.REGISTER_MEMBER, new RegisterForm());
            model.addAttribute("registerForm", new RegisterForm());
        } else {
            logger.info("registerForm: {}", registerForm);
            model.addAttribute("registerForm", registerForm);
        }

        return "members/registerForm";
    }

    @PostMapping(value = "/userIdChk")
    public String userIdChk(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member id check");

        HttpSession session = request.getSession(false);

        logger.info("userID: {}", form.getUserId());
        if (Objects.equals(form.getUserId(), "")) {
            result.rejectValue("userId", "Empty.userId");
        }

        String patternUserId = "(?=.*[0-9])(?=.*[a-zA-Z]).{5,12}";
        boolean regexUserId = Pattern.matches(patternUserId, form.getUserId());
        if (!result.hasFieldErrors("userId") && !regexUserId) {
            result.rejectValue("userId", "Format.userId");
        }

        Member member = Member.createMember(form.getUserId(), null, null, null, null);
        if (form.getUserId() != null && !memberService.findByUserID(member).isEmpty()) {
            result.rejectValue("userId", "UserId.exist");
        }

        if (result.hasErrors()) {
            form.setUserIdCheck(false);
            session.setAttribute(SessionConst.REGISTER_MEMBER, form);
            return "members/registerForm";
        }

        form.setUserIdCheck(true);
        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        rttr.addFlashAttribute("message", "아이디 중복 확인을 통과하였습니다.");

        logger.info("error: {}", result);
        return "redirect:/members/new";
    }

    @PostMapping(value = "/nicknameChk")
    public String nicknameChk(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member nickname check");

        HttpSession session = request.getSession(false);

        if (Objects.equals(form.getNickname(), "")) {
            result.rejectValue("nickname", "Empty.nickname");
        }

        String patternNickname = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$";
        boolean regexNickname = Pattern.matches(patternNickname, form.getNickname());
        if (!result.hasFieldErrors("nickname") && !regexNickname) {
            result.rejectValue("nickname", "Format.nickname");
        }

        Member member = Member.createMember(null, null, form.getNickname(), null, null);
        if (form.getNickname() != null && !memberService.findByNickname(member).isEmpty()) {
            result.rejectValue("nickname", "Nickname.exist");
        }

        if (result.hasErrors()) {
            form.setNicknameCheck(false);
            session.setAttribute(SessionConst.REGISTER_MEMBER, form);
            return "members/registerForm";
        }

        form.setNicknameCheck(true);
        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        rttr.addFlashAttribute("message", "닉네임 중복 확인을 통과하였습니다.");

        logger.info("error: {}", result);
        return "redirect:/members/new";
    }

    @PostMapping(value = "/emailChk")
    public String emailChk(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email check");

        HttpSession session = request.getSession(false);

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }

        String patternEmail = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        boolean regexEmail = Pattern.matches(patternEmail, form.getEmail());
        if (!result.hasFieldErrors("email") && !regexEmail) {
            result.rejectValue("email", "Format.email");
        }

        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if (form.getEmail() != null && !memberService.findByEmail(member).isEmpty()) {
            result.rejectValue("email", "Email.exist");
        }

        if (result.hasErrors()) {
            form.setEmailCheck(false);
            session.setAttribute(SessionConst.REGISTER_MEMBER, form);
            return "members/registerForm";
        }

        form.setEmailCheck(true);
        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        rttr.addFlashAttribute("message", "이메일 중복 확인을 통과하였습니다.");

        logger.info("error: {}", result);
        return "redirect:/members/new";
    }

    @PostMapping("/emailNumberSend")
    public String emailNumberSend(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email number send");

        HttpSession session = request.getSession(false);

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }
        if (!result.hasFieldErrors("email") && !form.isEmailCheck()) {
            result.rejectValue("email", "Empty.email.check");
        }

        if(result.hasErrors()) {
            form.setEmailNumberSend(false);
            session.setAttribute(SessionConst.REGISTER_MEMBER, form);
            return "members/registerForm";
        }

        mailSendService.joinEmail(form.getEmail());
        form.setEmailNumberSend(true);
        form.setTimeCount(1800L); // 인증번호 유효 기간: 30분
        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        rttr.addFlashAttribute("message", "해당 이메일로 인증번호 발송이 완료되었습니다. 확인 부탁드립니다.");

        return "redirect:/members/new";
    }

    @PostMapping("/emailNumberChk")
    public String emailNumberChk(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email number check");

        HttpSession session = request.getSession(false);

        if(!form.isEmailNumberSend()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.send");
        }
        if (!result.hasFieldErrors("emailNumber") && Objects.equals(form.getEmailNumber(), "")) {
            result.rejectValue("emailNumber", "Empty.emailNumber");
        }

        String patternEmailNumber = "^[0-9]*$";
        boolean regexEmailNumber = Pattern.matches(patternEmailNumber, form.getEmailNumber());
        if (!result.hasFieldErrors("emailNumber") && !regexEmailNumber) {
            result.rejectValue("emailNumber", "Format.emailNumber");
        }

        boolean isSame = mailSendService.CheckAuthNum(form.getEmail(), form.getEmailNumber());
        if (!result.hasFieldErrors("emailNumber") && !isSame) {
            result.rejectValue("emailNumber", "NotSame.emailNumber");
        }

        if (result.hasErrors()) {
            form.setEmailNumberCheck(false);
            session.setAttribute(SessionConst.REGISTER_MEMBER, form);
            return "members/registerForm";
        }

        form.setEmailNumberCheck(true);
        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        rttr.addFlashAttribute("message", "이메일 인증이 완료되었습니다.");

        return "redirect:/members/new";
    }

    @PostMapping("/register")
    public String postRegister(RegisterForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member password check and register");

        HttpSession session = request.getSession(false);

        if (Objects.equals(form.getPassword(), "")) {
            result.rejectValue("password", "Empty.password");
        }

        String patternPassword = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}";
        boolean regexPassword = Pattern.matches(patternPassword, form.getPassword());
        if (!result.hasFieldErrors("password") && !regexPassword) {
            result.rejectValue("password", "Format.password");
        }
        if (Objects.equals(form.getPasswordConfirm(), "")) {
            result.rejectValue("passwordConfirm", "Empty.passwordConfirm");
        }
        if (!result.hasFieldErrors("password") && !result.hasFieldErrors("passwordConfirm") && !Objects.equals(form.getPassword(), form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "NotSame.password");
        }

        if (Objects.equals(form.getUserId(), "")) {
            result.rejectValue("userId", "Empty.userId");
        }
        if (!result.hasFieldErrors("userId") && !form.isUserIdCheck()) {
            result.rejectValue("userId", "Empty.userId.check");
        }
        if (Objects.equals(form.getNickname(), "")) {
            result.rejectValue("nickname", "Empty.nickname");
        }
        if (!result.hasFieldErrors("nickname") && !form.isNicknameCheck()) {
            result.rejectValue("nickname", "Empty.nickname.check");
        }
        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }
        if (!result.hasFieldErrors("email") && !form.isEmailCheck()) {
            result.rejectValue("email", "Empty.email.check");
        }
        if(!form.isEmailNumberSend()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.send");
        }
        if (!result.hasFieldErrors("emailNumber") && Objects.equals(form.getEmailNumber(), "")) {
            result.rejectValue("emailNumber", "Empty.emailNumber");
        }
        if (!result.hasFieldErrors("emailNumber") && !form.isEmailNumberCheck()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.check");
        }

        if (result.hasFieldErrors("userId")) form.setUserIdCheck(false);
        if (result.hasFieldErrors("nickname")) form.setNicknameCheck(false);
        if (result.hasFieldErrors("email")) form.setEmailCheck(false);
        if (result.hasFieldErrors("emailNumber")) form.setEmailNumberCheck(false);

        session.setAttribute(SessionConst.REGISTER_MEMBER, form);
        if (result.hasErrors()) return "members/registerForm";

        Check check = Check.createCheck();
        check.changeEmailAuth(true);
        check.changeInformationAgree(true);
        Member newMember = Member.createMember(form.getUserId(), form.getPassword(), form.getNickname(), form.getEmail(), check);
        memberService.register(newMember);

        return "redirect:/members/registerComplete";
    }

    @GetMapping("/registerComplete")
    public String registerComplete(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();

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
        HttpSession session = request.getSession(false);
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
        HttpSession session = request.getSession(false);
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
        if (newMember == null) {
            result.rejectValue("nickname", "Nickname.exist");
            return "/members/myProfile";
        }

        session.setAttribute(SessionConst.LOGIN_MEMBER, newMember);
        rttr.addFlashAttribute("message", "닉네임을 변경했습니다.");

        return "redirect:/members/myProfile";
    }

    @GetMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
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
        HttpSession session = request.getSession(false);
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
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        return "members/leaveNotice";
    }

    @PostMapping("/leave")
    public String leaveService(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberService.deleteMember(member.getId());
        session.setAttribute(SessionConst.LOGIN_MEMBER, null);

        return "members/bye";
    }
}
