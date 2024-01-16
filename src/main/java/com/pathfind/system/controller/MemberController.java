package com.pathfind.system.controller;

import com.pathfind.system.domain.Check;
import com.pathfind.system.domain.Member;
import com.pathfind.system.dto.EmailRequestDto;
import com.pathfind.system.dto.FindPasswordForm;
import com.pathfind.system.dto.MemberForm;
import com.pathfind.system.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String validationChk(@Valid MemberForm form, BindingResult result) {
        logger.info("member id check");
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

    @PostMapping("returnId")
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
        if(result.hasErrors()) {
            logger.info("error: {}", result);
            return "members/findPassword";
        }
        try {
            memberService.idEmailChk(form.getUserId(), form.getEmail());
        } catch (Exception e) {
            result.addError(new FieldError("findPasswordForm", "userId", e.getMessage()));
            result.addError(new FieldError("findPasswordForm", "email", e.getMessage()));
        }

        return "members/findPassword";
    }

    @PostMapping("/returnPassword")
    public String returnPassword(FindPasswordForm form) {
        memberService.findPassword(form.getUserId(), form.getEmail());

        return "members/yourPassword";
    }
}
