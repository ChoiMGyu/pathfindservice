/*
 * 클래스 기능 : 클라이언트로부터 회원객체 생성을 위한 form
 * 최근 수정 일자 : 2024.01.15(월)
 */

package com.pathfind.system.dto;

import com.pathfind.system.domain.Check;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class MemberForm {

    //@Column(length = 12, nullable = false, unique = true)
    @NotEmpty(message = "아아디 입력은 필수입니다")
    private String userId; //아이디

    //@Column(length = 20, nullable = false)
    @NotEmpty(message = "비밀번호 입력은 필수입니다")
    private String password; //비밀번호 // 변경 가능

    //@Column(length = 12, nullable = false, unique = true)
    @NotEmpty(message = "닉네임 입력은 필수입니다")
    private String nickname; //닉네임 // 변경 가능

    //@Column(length = 45, nullable = false, unique = true)
    @NotEmpty(message = "이메일 입력은 필수입니다")
    private String email; //이메일
}
