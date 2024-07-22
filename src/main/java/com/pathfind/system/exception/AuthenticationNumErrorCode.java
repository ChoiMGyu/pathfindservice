///*
// * 클래스 기능 : 회원 가입 시 인증 번호 발급에 관한 Error를 정리한 enum class
// * 최근 수정 일자 : 2024.07.22(월)
// */
//package com.pathfind.system.exception;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@Getter
//@AllArgsConstructor
//public enum AuthenticationNumErrorCode implements BasicErrorCode {

//
//    private final int status;
//    private final String code;
//    private final String description;
//
//    @Override
//    public ErrorReason getErrorReason() {
//        return ErrorReason.builder().description(description).code(code).status(status).build();
//    }
//
//    @Override
//    public ErrorVCResponse getErrorVCResponse() {
//        return new ErrorVCResponse(code, description);
//    }
//}
