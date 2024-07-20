/*
 * 클래스 기능 : 회원 가입 시 이메일 검증 관련 에러 코드 enum class
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailCheckErrorCode implements BasicErrorCode {
    INVALID_INPUT_VALUE(409, "EMAIL_INVALID", "이메일 형식이 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(409, "EMAIL_EXIST", "이미 존재하는 이메일입니다.");

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
