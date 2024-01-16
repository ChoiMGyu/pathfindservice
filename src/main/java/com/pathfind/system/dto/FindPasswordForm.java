package com.pathfind.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FindPasswordForm {
    @NotEmpty(message = "아이디는 필수입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{5,12}", message = "아이디는 5~12자 영문 대 소문자, 숫자를 사용하세요.")
    private String userId; //아이디
    @NotEmpty(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email; //이메일
}
