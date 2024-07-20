/*
 * 클래스 기능 : 회원 가입 관련 확인 성공 시 반환할 메시지들을 모아놓는 클래스
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.api;

public abstract class CheckSuccess {
    public static final String USER_ID = "아이디 중복 확인을 통과하였습니다.";
    public static final String NICKNAME = "닉네임 중복 확인을 통과하였습니다.";
    public static final String EMAIL = "이메일 중복 확인을 통과하였습니다.";
}
