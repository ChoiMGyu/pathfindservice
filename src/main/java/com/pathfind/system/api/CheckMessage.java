/*
 * 클래스 기능 : 회원 가입 관련 확인 성공 시 반환할 메시지들을 모아놓는 클래스
 * 최근 수정 일자 : 2024.08.02(금)
 */
package com.pathfind.system.api;

public abstract class CheckMessage {
    public static final String USER_ID = "아이디 중복 확인을 통과하였습니다.";
    public static final String NICKNAME = "닉네임 중복 확인을 통과하였습니다.";
    public static final String EMAIL = "이메일 중복 확인을 통과하였습니다.";
    public static final String AUTHENTICATION_NUM = "인증 번호를 발급하였습니다.";
    public static final String AUTHENTICATION_CHK = "인증 번호 확인을 통과하였습니다.";
    public static final String REGISTER_SUCCESS = "회원 가입을 축하드립니다.";
    public static final String PASSWORD = "비밀 번호 확인을 통과하였습니다.";

    public static final String REGISTER_FAIL = "회원 가입을 실패하였습니다.";
}
