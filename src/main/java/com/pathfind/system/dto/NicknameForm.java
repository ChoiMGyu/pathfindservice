package com.pathfind.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NicknameForm {
    @NotEmpty(message = "닉네임은 필수입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~12자리여야 합니다.")
    private String nickname; //닉네임
}
