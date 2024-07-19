package com.pathfind.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BasicErrorCode {
    EXCEPTION_EXAMPLE_1(401, "exception_ex_1", "에러 예시1"),
    EXCEPTION_EXAMPLE_2(401, "exception_ex_2", "에러 예시2"),
    EXCEPTION_EXAMPLE_3(401, "exception_ex_3", "에러 예시3"),
    EXCEPTION_EXAMPLE_4(403, "exception_ex_4", "에러 예시4"),
    EXCEPTION_EXAMPLE_5(403, "exception_ex_5", "에러 예시5"),
    EXCEPTION_EXAMPLE_6(403, "exception_ex_6", "에러 예시6");

    private final int status;
    private final String code;
    private final String description;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().description(description).code(code).status(status).build();
    }
}
