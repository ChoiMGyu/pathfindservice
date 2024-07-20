/*
 * 클래스 기능 : 중복 확인 시 사용하는 exception class
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.exception;

import lombok.Getter;

@Getter
public class ValidationException extends CustomException {
    public ValidationException(BasicErrorCode errorCode) {
        super(errorCode, errorCode.getDescription());
    }
}
