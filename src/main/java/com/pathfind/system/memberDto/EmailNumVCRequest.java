/*
 * 클래스 기능 : 입력한 이메일로 인증번호를 발급하기 위한 DTO
 * 최근 수정 일자 : 2024.07.22(월)
 */
package com.pathfind.system.memberDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "EmailNumVCRequest", description = "입력한 이메일로 인증번호를 발급하기 위한 DTO")
@Getter
@Setter
public class EmailNumVCRequest {

    @Email
    //1)@기호를 포함해야 한다
    //2)@기호를 기준으로 이메일 주소를 이루는 로컬호스트와 도메인 파트가 존재해야 한다
    //3)도메인 파트는 최소하나의 점과 그 뒤에 최소한 2개의 알파벳을 가진다를 검증
    @Schema(name = "email", description = "유저 이메일", example = "qwer1234@mailSvc.abc")
    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

    @Schema(name = "email", description = "이메일 유효성 검증 여부")
    private boolean emailCheck; //이메일 유효성 검증 여부
}
