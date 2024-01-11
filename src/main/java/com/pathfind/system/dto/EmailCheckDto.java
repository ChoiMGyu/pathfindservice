/*
 * 클래스 기능 : 사용자의 이메일과 사용자의 이메일을 받아오는 DTO
 * 최근 수정 일자 : 2024.01.10(수)
 */
package com.pathfind.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailCheckDto {
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String authNum;
}
