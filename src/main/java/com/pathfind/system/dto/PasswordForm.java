package com.pathfind.system.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @NotEmpty(message = "기존 비밀번호 입력은 필수입니다")
    @Length(max = 20)
    private String oldPassword;

    @NotEmpty(message = "새로운 패스워드 입력은 필수입니다")
    @Length(max = 20)
    private String newPassword1;

    @NotEmpty(message = "새로운 패스워드 확인란 입력은 필수입니다")
    @Length(max = 20)
    private String newPassword2;
}
