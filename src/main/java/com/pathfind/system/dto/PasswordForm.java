/*
 * 클래스 기능 : 비밀번호 변경을 수행하기 위한 form
 * 최근 수정 일자 : 2024.01.17(수)
 */
package com.pathfind.system.dto;

import com.pathfind.system.validation.ValidationGroups;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @NotEmpty(message = "기존 비밀번호 입력은 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    @Length(max = 20)
    private String oldPassword;

    @NotEmpty(message = "새로운 패스워드 입력은 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    @Length(max = 20)
    //@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}", message = "새로운 비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.", groups = ValidationGroups.PatternCheckGroup.class)
    private String newPassword1;

    @NotEmpty(message = "새로운 패스워드 확인 입력은 필수입니다", groups = ValidationGroups.NotEmptyGroup.class)
    //message 내용 수정
    @Length(max = 20)
    private String newPassword2;
}
