/*
 * 클래스 기능 : 회원 가입 시 아이디 검증 관련 에러 코드 enum class
 * 최근 수정 일자 : 2024.08.04(일)
 */
package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserIdCheckErrorCode implements BasicErrorCode {
    NOT_EMPTY(400, "NotEmpty", "아이디는 필수입니다."),
    PATTERN(400, "Pattern", "아이디는 5~12자 영문 대 소문자, 숫자를 사용하세요."),
    ID_ALREADY_EXISTS(409, "USER_ID_EXIST", "이미 존재하는 아이디입니다.");

    private final int status;
    private final String code;
    private final String description;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().description(description).code(code).status(status).build();
    }

    @Override
    public ErrorVCResponse getErrorVCResponse() {
        return new ErrorVCResponse(code, description);
    }
}
