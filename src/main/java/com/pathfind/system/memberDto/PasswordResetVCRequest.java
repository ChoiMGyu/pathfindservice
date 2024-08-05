/*
 * 클래스 기능 : 사용자로부터 받은 아이디, 이메일, 이메일 인증 번호의 일치 여부 확인을 위해 사용되는 dto 클래스
 * 최근 수정 일자 : 2024.08.03(토)
 */
package com.pathfind.system.memberDto;

import com.pathfind.system.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(name = "PasswordResetVCRequest", description = "유저 아이디, 이메일, 이메일 인증 번호 일치 여부 확인에 쓰이는 dto")
@Data
public class PasswordResetVCRequest {
    @Schema(name = "userId", description = "유저 아이디", example = "qwer1234")
    @NotEmpty(message = "아이디는 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[0-9a-zA-Z]{5,12}$", message = "아이디는 5~12자 영문 대 소문자, 숫자를 사용하세요.", groups = ValidationGroups.PatternGroup.class)
    private String userId;
    @Schema(name = "email", description = "유저 이메일", example = "qwer1234@mailSvc.abc")
    @NotEmpty(message = "이메일은 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.", groups = ValidationGroups.PatternGroup.class)
    private String email; // 유저 아이디
    @Schema(name = "emailNumber", description = "입력받는 인증번호")
    @NotEmpty(message = "인증 번호를 입력해주세요.", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[0-9]*$", message = "인증번호는 숫자만 입력해주세요.", groups = ValidationGroups.PatternGroup.class)
    private String emailNumber; //입력받은 인증번호
}
