/*
 * 클래스 기능 : 입력 받은 인증 번호와 발급된 인증 번호가 동일한지 확인하는 DTO
 * 최근 수정 일자 : 2024.08.03(토)
 */
package com.pathfind.system.memberDto;

import com.pathfind.system.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(name = "FindUserIdVCRequest", description = "입력 받은 인증 번호와 발급된 인증 번호의 일치 여부를 확인하는 DTO")
@Data
public class FindUserIdVCRequest {
    @Schema(name = "email", description = "유저 이메일", example = "qwer1234@mailSvc.abc")
    @NotEmpty(message = "이메일은 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.", groups = ValidationGroups.PatternGroup.class)
    private String email; // 유저 이메일
    @Schema(name = "emailNumber", description = "입력받은 인증번호")
    @NotEmpty(message = "인증 번호를 입력해주세요.", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[0-9]*$", message = "인증번호는 숫자만 입력해주세요.", groups = ValidationGroups.PatternGroup.class)
    private String emailNumber; //입력받은 인증번호
}
