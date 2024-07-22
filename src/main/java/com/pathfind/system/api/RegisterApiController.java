/*
 * 클래스 기능 : 회원 가입 API Controller
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.api;

import com.pathfind.system.customAnnotation.ApiErrorCode;
import com.pathfind.system.domain.Member;
import com.pathfind.system.exception.EmailCheckErrorCode;
import com.pathfind.system.exception.NicknameCheckErrorCode;
import com.pathfind.system.exception.UserIdCheckErrorCode;
import com.pathfind.system.exception.ValidationException;
import com.pathfind.system.memberDto.*;
import com.pathfind.system.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 가입 API", description = "회원 가입 시 사용되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/registration/*")
public class RegisterApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;

    // 아이디 유효성, 중복 확인 여부를 검사하는 함수이다.
    @Operation(summary = "유저 아이디 유효성 검증, 중복 여부 확인", description = "회원 가입 시 유저 아이디 유효성, 중복 여부를 확인하고 그 결과를 반환합니다.")
    @ApiErrorCode(UserIdCheckErrorCode.class)
    @GetMapping("check/user-id")
    public UserIdCheckVCResponse userIdChk(@Parameter(name = "userId", description = "문자열 형태의 유저 아이디") @ModelAttribute(name = "userId") @Valid UserIdVCRequest form) {
        logger.info("member user id check");

        logger.info("{}", form.getUserId());
        Member member = Member.createMember(form.getUserId(), null, null, null, null);
        if (!memberService.findByUserId(member).isEmpty()) {
            throw new ValidationException(UserIdCheckErrorCode.ID_ALREADY_EXISTS);
        }

        return new UserIdCheckVCResponse(true, CheckSuccess.USER_ID);
    }

    // 닉네임 유효성, 중복 확인 여부를 검사하는 함수이다.
    @Operation(summary = "닉네임 유효성 검증, 중복 여부 확인", description = "회원 가입 시 닉네임 유효성, 중복 여부를 확인하고 그 결과를 반환합니다.")
    @ApiErrorCode(NicknameCheckErrorCode.class)
    @GetMapping("check/nickname")
    public NicknameCheckVCResponse nicknameChk(@Parameter(name = "nickname", description = "문자열 형태의 닉네임") @ModelAttribute(name = "nickname") @Valid NicknameVCRequest form) {
        logger.info("member nickname check");

        logger.info("{}", form.getNickname());
        Member member = Member.createMember(null, null, form.getNickname(), null, null);
        if (!memberService.findByNickname(member).isEmpty()) {
            throw new ValidationException(NicknameCheckErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        return new NicknameCheckVCResponse(true,CheckSuccess.NICKNAME);
    }

    // 이메일 유효성, 중복 확인 여부를 검사하는 함수이다.
    @Operation(summary = "이메일 유효성 검증, 중복 여부 확인", description = "회원 가입 시 이메일 유효성, 중복 여부를 확인하고 그 결과를 반환합니다.")
    @ApiErrorCode(EmailCheckErrorCode.class)
    @GetMapping("check/email")
    public EmailCheckVCResponse emailChk(@Parameter(name = "email", description = "문자열 형태의 이메일") @ModelAttribute(name = "email") @Valid EmailVCRequest form) {
        logger.info("member email check");

        logger.info("{}", form.getEmail());
        Member member = Member.createMember(null, null, null, form.getEmail(), null);
        if (!memberService.findByEmail(member).isEmpty()) {
            throw new ValidationException(EmailCheckErrorCode.EMAIL_ALREADY_EXISTS);
        }

        return new EmailCheckVCResponse(true,CheckSuccess.EMAIL);
    }
}
