package com.pathfind.system.dto;

import lombok.Data;

@Data
public class SubmitForm {
    private String userId; // 아이디
    private String nickname; // 닉네임
    private String email; // 이메일
    private boolean UserIdCheck; // 아이디 유효성 검증 여부
    private boolean NicknameCheck; // 닉네임 유효성 검증 여부
    private boolean EmailCheck; // 이메일 유효성 검증 여부
    private String emailNumber; // 이메일 인증 번호
    private Long timeCount; // 이메일 인증 시간
    private boolean emailNumberSend; // 이메일 인증 번호 발급 여부
    private boolean EmailNumberCheck; // 이메일 인증 번호 검증 여부
    private String password; // 비밀번호
    private String passwordConfirm; // 비밀번호 확인
}
