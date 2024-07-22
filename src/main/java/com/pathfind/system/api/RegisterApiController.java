/*
 * 클래스 기능 : 회원 가입 API Controller
 * 최근 수정 일자 : 2024.07.22(월)
 */
package com.pathfind.system.api;

import com.pathfind.system.customAnnotation.ApiErrorCode;
import com.pathfind.system.domain.Member;
import com.pathfind.system.exception.*;
import com.pathfind.system.memberDto.EmailChkVCRequest;
import com.pathfind.system.memberDto.EmailChkVCResponse;
import com.pathfind.system.memberDto.EmailNumVCRequest;
import com.pathfind.system.memberDto.EmailNumVCResponse;
import com.pathfind.system.registerDto.*;
import com.pathfind.system.service.MailSendService;
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

    private static final Logger logger = LoggerFactory.getLogger(RegisterApiController.class);

    private final MemberService memberService;

    private final MailSendService mailSendService;

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

        return new EmailCheckVCResponse(true, CheckSuccess.EMAIL);
    }

    @Operation(summary = "이메일로 인증 번호 전송", description = "회원 가입 시 중복 확인을 거친 이메일의 유효성 여부를 확인하고 그 이메일로 인증 번호를 전송합니다.")
    @PostMapping("emailNumberSend")
    public EmailNumVCResponse emailNumberSend(@RequestBody @Valid EmailNumVCRequest request) {
        logger.info("이메일 인증 번호 발급 api 호출");
        //emailNumberValidation();
        String authNumber = mailSendService.joinEmail(request.getEmail());
        //logger.info("authNumber : " + authNumber);
        return new EmailNumVCResponse(authNumber, 1L, CheckSuccess.AUTHENTICATION_NUM);
    }


    @Operation(summary = "인증 번호 동일성 여부 검사", description = "회원 가입 시 이메일로 전송된 인증 번호와 입력한 인증 번호가 동일한지 확인하고 그 결과를 반환합니다.")
    @ApiErrorCode(AuthenticationChkErrorCode.class)
    @PostMapping("emailNumberChk")
    public EmailChkVCResponse emailNumberChk(@RequestBody @Valid EmailChkVCRequest request) {
        logger.info("이메일 인증 번호를 확인 api 호출");
        boolean chk = mailSendService.CheckAuthNum(request.getEmail(), request.getAuthNum());
        if(!chk) {
            throw new ValidationException(AuthenticationChkErrorCode.NOT_SAME);
        }
        return new EmailChkVCResponse(chk, CheckSuccess.AUTHENTICATION_CHK);
    }
}
