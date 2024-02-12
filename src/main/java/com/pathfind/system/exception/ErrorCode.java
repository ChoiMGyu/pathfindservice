/*
 * 클래스 기능 : 예외 발생 enum 클래스
 * 최근 수정 일자 : 2024.02.09(금)
 */
package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "-400", "올바른 값을 입력하지 않은 경우");

    private final int status;
    private final String code;
    private final String description;
}
