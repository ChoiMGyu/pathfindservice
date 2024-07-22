/*
 * 클래스 기능 : 회원 가입 시 인증 번호 확인에 관한 Error를 정리한 enum class
 * 최근 수정 일자 : 2024.07.22(월)
 */
package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationChkErrorCode implements BasicErrorCode {
    INVALID_INPUT_VALUE(409, "AUTH_CHK_INVALID", "인증 번호를 입력해 주세요"),
    NOT_SAME(409, "AUTH_NOT_SAME", "입력하신 인증 번호가 일치하지 않습니다.");

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


