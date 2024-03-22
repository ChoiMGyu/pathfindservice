/*
 * 클래스 기능 : 회원 관련 페이지 렌더링을 하는 controller
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.Check;
import com.pathfind.system.domain.Member;
import com.pathfind.system.memberDto.*;
import com.pathfind.system.service.MailSendService;
import com.pathfind.system.service.MemberService;
import com.pathfind.system.validation.ValidationSequence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    private final MailSendService mailSendService;

    // 컨트롤러를 호출한 URI의 파일 경로만 추출해 반환하는 함수이다.
    private String getPath(HttpServletRequest request) {
        String path = null;
        try {
            path = new URI(request.getHeader("REFERER")).getPath();
            logger.info("getPath: {}", path);
        } catch (URISyntaxException e) {
            logger.info("URISyntaxException: {}", e.getMessage());
        }
        return path;
    }

    // submitForm을 model에 추가하는 함수이다.
    private void addSubmitForm(Model model, HttpSession session) {
        SubmitForm submitForm = (SubmitForm) session.getAttribute(SessionConst.SUBMIT_MEMBER);
        if (submitForm == null) {
            session.setAttribute(SessionConst.SUBMIT_MEMBER, new SubmitForm());
            model.addAttribute("submitForm", new SubmitForm());
        } else {
            model.addAttribute("submitForm", submitForm);
        }
    }

    // 이메일 인증번호의 유효성, 동일한지 여부를 검사하는 함수이다.
    private void emailNumberValidation(SubmitForm form, BindingResult result) {
        if (!form.isEmailNumberSend()) {
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
    }

    // 약관 동의 페이지를 반환하는 함수이다.
    @GetMapping(value = "/agree")
    public String getAgree(HttpServletRequest request) {
        logger.info("get register agree");
        HttpSession session = request.getSession();

        return "members/agree";
    }

    // 회원 가입 양식 페이지를 반환하는 함수이다.
    @GetMapping(value = "/new")
    public String getRegister(HttpServletRequest request, Model model) {
        logger.info("get register form");

        HttpSession session = request.getSession();
        addSubmitForm(model, session);

        return "members/registerForm";
    }

    // 아이디 유효성, 중복 확인 여부를 검사하는 함수이다.
    @PostMapping(value = "/userIdChk")
    public String userIdChk(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member user id check");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

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

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setUserIdCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            /*logger.info("getRemoteURI: {}", request.getRequestURI());
            logger.info("getHeader: {}", request.getHeader("REFERER"));
            logger.info("getRequestURL: {}", request.getRequestURL());*/
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setUserIdCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "아이디 중복 확인을 통과하였습니다.");

        return "redirect:" + path;
    }

    // 닉네임 유효성, 중복 확인 여부를 검사하는 함수이다.
    @PostMapping(value = "/nicknameChk")
    public String nicknameChk(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member nickname check");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

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

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setNicknameCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setNicknameCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "닉네임 중복 확인을 통과하였습니다.");

        return "redirect:" + path;
    }

    // 이메일 유효성, 중복 확인 여부를 검사하는 함수이다.
    @PostMapping(value = "/emailChk")
    public String emailChk(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email check");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

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

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setEmailCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setEmailCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "이메일 중복 확인을 통과하였습니다.");

        return "redirect:" + path;
    }

    // 이메일 인증번호를 보내는 함수이다.
    @PostMapping("/emailNumberSend")
    public String emailNumberSend(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email number send");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }
        if (!result.hasFieldErrors("email") && !form.isEmailCheck()) {
            result.rejectValue("email", "Empty.email.check");
        }

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setEmailNumberSend(false);
            form.setEmailNumberCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        mailSendService.joinEmail(form.getEmail());
        form.setEmailNumberSend(true);
        form.setEmailNumberCheck(false);
        form.setTimeCount(1800L); // 인증번호 유효 기간: 30분
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "해당 이메일로 인증번호 발송이 완료되었습니다. 확인 부탁드립니다.");

        return "redirect:" + path;
    }

    // 사용자로부터 입력받은 인증번호와 Redis의 인증번호가 동일한지 여부를 검사하는 함수이다.
    @PostMapping("/emailNumberChk")
    public String emailNumberChk(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member email number check");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        emailNumberValidation(form, result); // 사용자가 보낸 인증번호의 유효성, Redis 인증번호와의 동일성 여부를 판단한다.

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setEmailNumberCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setEmailNumberCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "이메일 인증이 완료되었습니다.");

        return "redirect:" + path;
    }

    // 회원 가입을 진행하여 데이터베이스에 회원 정보를 저장하는 함수이다.
    @PostMapping("/register")
    public String postRegister(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member password check and register");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

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
        if (result.hasFieldErrors("userId")) form.setUserIdCheck(false);

        if (Objects.equals(form.getNickname(), "")) {
            result.rejectValue("nickname", "Empty.nickname");
        }
        if (!result.hasFieldErrors("nickname") && !form.isNicknameCheck()) {
            result.rejectValue("nickname", "Empty.nickname.check");
        }
        if (result.hasFieldErrors("nickname")) form.setNicknameCheck(false);

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }
        if (!result.hasFieldErrors("email") && !form.isEmailCheck()) {
            result.rejectValue("email", "Empty.email.check");
        }
        if (result.hasFieldErrors("email")) form.setEmailCheck(false);

        if (!form.isEmailNumberSend()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.send");
        }
        if (!result.hasFieldErrors("emailNumber") && Objects.equals(form.getEmailNumber(), "")) {
            result.rejectValue("emailNumber", "Empty.emailNumber");
        }
        if (!result.hasFieldErrors("emailNumber") && !form.isEmailNumberCheck()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.check");
        }
        if (result.hasFieldErrors("emailNumber")) form.setEmailNumberCheck(false);

        String path = getPath(request);

        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        if (result.hasErrors()) {
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        Check check = Check.createCheck();
        check.changeEmailAuth(true);
        check.changeInformationAgree(true);
        Member newMember = Member.createMember(form.getUserId(), form.getPassword(), form.getNickname(), form.getEmail(), check);
        memberService.register(newMember);

        return "redirect:/members/registerComplete";
    }

    // 회원 가입 완료 페이지를 반환하는 함수이다.
    @GetMapping("/registerComplete")
    public String registerComplete(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();

        return "members/registerComplete";
    }

    // 아이디 찾기 페이지를 반환하는 함수이다.
    @GetMapping("/findUserId")
    public String findUserId(HttpServletRequest request, Model model) {
        logger.info("find userId");

        HttpSession session = request.getSession();
        addSubmitForm(model, session);

        return "members/findUserId";
    }

    // 이메일 유효성, 중복 확인 여부를 검사하는 함수이다.
    @PostMapping("/isValidEmail")
    public String isValidEmail(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }

        String patternEmail = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        boolean regexEmail = Pattern.matches(patternEmail, form.getEmail());
        if (!result.hasFieldErrors("email") && !regexEmail) {
            result.rejectValue("email", "Format.email");
        }

        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if (!result.hasFieldErrors("email") && memberService.findByEmail(member).isEmpty()) {
            result.rejectValue("email", "Email.notExist");
        }

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setEmailNumberSend(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setEmailCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "이메일 중복 확인을 통과하였습니다.");

        logger.info("email validation error: {}", result);

        return "redirect:" + path;
    }

    // 사용자에게 아이디를 포함한 페이지를 반환하는 함수이다.
    @PostMapping("/returnId")
    public String returnId(SubmitForm form, Model model, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        emailNumberValidation(form, result);

        String path = getPath(request);

        List<String> userId = memberService.findUserIdByEmail(form.getEmail());
        if (!userId.isEmpty()) {
            model.addAttribute("userId", userId.get(0));
        } else {
            result.rejectValue("userId", "Fail.find.userId");
        }

        if (result.hasErrors()) {
            form.setEmailNumberCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        session.invalidate();

        return "members/yourUserId";
    }

    // 비밀번호 초기화 페이지를 반환하는 함수이다.
    @GetMapping("/findPassword")
    public String findPassword(HttpServletRequest request, Model model) {
        logger.info("find password");

        HttpSession session = request.getSession();
        addSubmitForm(model, session);

        return "members/findPassword";
    }

    // 아이디와 이메일의 유효성과 데이터베이스에 해당 정보가 존재하는지 여부를 검사하는 함수이다.
    @PostMapping("/isValidIdEmail")
    public String isValidIdEmail(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        if (Objects.equals(form.getUserId(), "")) {
            result.rejectValue("userId", "Empty.userId");
        }

        String patternUserId = "(?=.*[0-9])(?=.*[a-zA-Z]).{5,12}";
        boolean regexUserId = Pattern.matches(patternUserId, form.getUserId());
        if (!result.hasFieldErrors("userId") && !regexUserId) {
            result.rejectValue("userId", "Format.userId");
        }

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }

        String patternEmail = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        boolean regexEmail = Pattern.matches(patternEmail, form.getEmail());
        if (!result.hasFieldErrors("email") && !regexEmail) {
            result.rejectValue("email", "Format.email");
        }

        if (!result.hasFieldErrors("userId") && !result.hasFieldErrors("email") && !memberService.idEmailChk(form.getUserId(), form.getEmail())) {
            result.reject("NotSame.userInfo");
        }
        logger.info("errors: {}", result);

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setUserIdCheck(false);
            form.setEmailCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        form.setUserIdCheck(true);
        form.setEmailCheck(true);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        rttr.addFlashAttribute("message", "회원 정보 확인을 통과하였습니다.");

        return "redirect:" + path;
    }

    // 회원의 비밀번호를 초기화하고 초기화된 비밀번호를 이메일로 전송하는 함수이다.
    @PostMapping("/returnPassword")
    public String returnPassword(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        logger.info("member password reset");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        if (Objects.equals(form.getUserId(), "")) {
            result.rejectValue("userId", "Empty.userId");
        }
        if (!result.hasFieldErrors("userId") && !form.isUserIdCheck()) {
            result.rejectValue("userId", "Empty.userId.check");
        }
        if (result.hasFieldErrors("userId")) form.setUserIdCheck(false);

        if (Objects.equals(form.getEmail(), "")) {
            result.rejectValue("email", "Empty.email");
        }
        if (!result.hasFieldErrors("email") && !form.isEmailCheck()) {
            result.rejectValue("email", "Empty.email.check");
        }
        if (result.hasFieldErrors("email")) form.setEmailCheck(false);

        if (!form.isEmailNumberSend()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.send");
        }
        if (!result.hasFieldErrors("emailNumber") && Objects.equals(form.getEmailNumber(), "")) {
            result.rejectValue("emailNumber", "Empty.emailNumber");
        }
        if (!result.hasFieldErrors("emailNumber") && !form.isEmailNumberCheck()) {
            result.rejectValue("emailNumber", "Empty.emailNumber.check");
        }
        if (result.hasFieldErrors("emailNumber")) form.setEmailNumberCheck(false);

        String path = getPath(request);

        session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
        if (result.hasErrors()) {
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }
        memberService.findPassword(form.getUserId(), form.getEmail());

        return "members/yourPassword";
    }

    @GetMapping("/updatePassword")
    public String updatePasswordForm(Model model) {
        logger.info("get updatePasswordForm");
        model.addAttribute("passwordForm", new PasswordForm());
        return "members/updatePasswordForm";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@Validated(ValidationSequence.class) PasswordForm form, BindingResult result, Model model, HttpSession session, RedirectAttributes rttr) {
        if (result.hasErrors()) {
            return "members/updatePasswordForm";
        }

        //검증할 패스워드 정규식 패턴
        String regex = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}";
        Pattern pattern = Pattern.compile(regex);

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        logger.info("로그인 멤버의 아이디 : " + loginMember.getId() + ", 패스워드 : " + loginMember.getPassword());

        Matcher matcher = pattern.matcher(form.getNewPassword1());

        if (!form.getOldPassword().equals(loginMember.getPassword())) {
            logger.info("이전 패스워드가 일치하지 않는 경우");
            result.rejectValue("oldPassword", "NotSame.newPassword1");
        }
        if (!form.getNewPassword1().equals(form.getNewPassword2())) {
            logger.info("새 패스워드와 새 패스워드 확인이 일치하지 않는 경우");
            result.rejectValue("newPassword2", "NotSame.newPassword2");
        }
        if (!matcher.matches()) {
            logger.info("새 패스워드 형식이 올바르지 않은 경우");
            result.rejectValue("newPassword2", "Format.newPassword2");
        }

        if (!result.hasErrors()) {
            logger.info("패스워드를 정상적으로 변경했습니다.");
            //logger.info("controller에서 비밀번호 변경을 하기 전 loginMember 객체 : " + loginMember);
            //비밀번호 변경을 하기 전 loginMember와 서비스 계층 로직 내에서 비밀번호 변경을 한 loginMember는 동일함
            loginMember = memberService.updatePassword(loginMember.getId(), form.getOldPassword(), form.getNewPassword1(), form.getNewPassword2());
            //그러나 비밀번호 변경을 한 후 반환된 loginMember는 이전의 loginMember와는 다른 객체임
            //logger.info("controller에서 비밀번호 변경을 한 후 loginMember : " + loginMember);
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
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
        session.setMaxInactiveInterval(60 * 60 * 3);

        //로그인 시도 후 세션을 만들고 휴면 계정 여부 체크
        if (loginMember.getCheck().isDormant()) {
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

    // 나의 프로필 페이지를 반환하는 함수이다.
    @GetMapping("/myProfile")
    public String getProfile(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, null);
        MemberForm memberForm = new MemberForm();
        memberForm.setUserId(member.getUserId());
        memberForm.setEmail(member.getEmail());
        NicknameForm nicknameForm = new NicknameForm();
        nicknameForm.setNickname(member.getNickname());

        model.addAttribute("memberForm", memberForm);
        model.addAttribute("nicknameForm", nicknameForm);

        return "members/myProfile";
    }

    // 나의 프로필 페이지에서 닉네임을 변경할 수 있게 하는 함수이다.
    @PostMapping("/updateNickname")
    public String updateNickname(MemberForm memberForm, @Valid NicknameForm nicknameForm, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberForm.setUserId(member.getUserId());
        memberForm.setEmail(member.getEmail());

        String patternNickname = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$";
        boolean regexNickname = Pattern.matches(patternNickname, nicknameForm.getNickname());
        if (!result.hasFieldErrors("nickname") && !regexNickname) {
            result.rejectValue("nickname", "Format.nickname");
        }

        Member findMember = Member.createMember(null, null, nicknameForm.getNickname(), null, null);
        if (nicknameForm.getNickname() != null && !memberService.findByNickname(findMember).isEmpty()) {
            result.rejectValue("nickname", "Nickname.exist");
        }

        if (result.hasErrors()) {
            return "members/myProfile";
        }

        Optional<Member> newMember = memberService.updateNickname(member.getId(), nicknameForm.getNickname());
/*        logger.info("newMember: {}", newMember.get());
        logger.info("newMember - isDormant: {}", newMember.get().getCheck().isDormant());
        logger.info("newMember: {}", newMember.get());*/
        if (newMember.isEmpty()) {
            result.rejectValue("nickname", "Nickname.exist");
            return "members/myProfile";
        }

        session.setAttribute(SessionConst.LOGIN_MEMBER, newMember.get());
        rttr.addFlashAttribute("message", "닉네임을 변경했습니다.");

        return "redirect:/members/myProfile";
    }

    // 이메일 변경 페이지를 반환하는 함수이다.
    @GetMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, Model model) {
        logger.info("member update email");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        addSubmitForm(model, session);

        return "members/updateEmail";
    }

    // 이메일의 유효성, 중복 여부를 검사하고 사용자의 이메일 변경을 수행하는 함수이다.
    @PostMapping("/updateEmail")
    public String updateEmail(SubmitForm form, BindingResult result, HttpServletRequest request, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        emailNumberValidation(form, result);

        String path = getPath(request);

        if (result.hasErrors()) {
            form.setEmailNumberCheck(false);
            session.setAttribute(SessionConst.SUBMIT_MEMBER, form);
            rttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "submitForm", result);
            return "redirect:" + path;
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        member = memberService.updateEmail(member.getId(), form.getEmail());
        rttr.addFlashAttribute("message", "이메일을 변경했습니다.");
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        session.setAttribute(SessionConst.SUBMIT_MEMBER, null);

        return "redirect:/members/myProfile";
    }

    // 탈퇴 안내 페이지를 반환하는 함수이다.
    @GetMapping("/leave")
    public String leaveNotice(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        return "members/leaveNotice";
    }

    // 사용자의 회원 탈퇴를 수행하고 탈퇴 완료 페이지를 반환하는 함수이다.
    @PostMapping("/leave")
    public String leaveService(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/members/login";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        memberService.deleteMember(member.getId());
        session.invalidate();

        return "members/bye";
    }
}
