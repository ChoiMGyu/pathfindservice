/*
 * 클래스 기능 : 회원 가입 시 닉네임 검증 관련 에러 코드 enum class
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NicknameCheckErrorCode implements BasicErrorCode {
    INVALID_INPUT_VALUE(409, "NICKNAME_INVALID", "닉네임은 특수문자를 제외한 2~12자리여야 합니다."),
    NICKNAME_ALREADY_EXISTS(409, "NICKNAME_EXIST", "이미 존재하는 닉네임입니다.");

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
