/*
 * 클래스 기능 : 예외 발생했을 때 응답을 처리하는 클래스
 * 최근 수정 일자 : 2024.07.22(월)
 */
package com.pathfind.system.exception;

import com.pathfind.system.memberDto.EmailChkVCRequest;
import com.pathfind.system.memberDto.EmailNumVCRequest;
import com.pathfind.system.registerDto.EmailVCRequest;
import com.pathfind.system.registerDto.NicknameVCRequest;
import com.pathfind.system.registerDto.UserIdVCRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorVCResponse>> handleValidException(MethodArgumentNotValidException e) {
        if (e.getBindingResult().getTarget() instanceof UserIdVCRequest) {
            return handleValidationException(new ValidationException(UserIdCheckErrorCode.INVALID_INPUT_VALUE));
        }
        else if (e.getBindingResult().getTarget() instanceof NicknameVCRequest) {
            return handleValidationException(new ValidationException(NicknameCheckErrorCode.INVALID_INPUT_VALUE));
        }
        else if (e.getBindingResult().getTarget() instanceof EmailVCRequest) {
            return handleValidationException(new ValidationException(EmailCheckErrorCode.INVALID_INPUT_VALUE));
        }
        else if (e.getBindingResult().getTarget() instanceof EmailChkVCRequest) {
            logger.info("인증 번호 확인에 문제가 생김");
            return handleValidationException(new ValidationException(AuthenticationChkErrorCode.INVALID_INPUT_VALUE));
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        BindingResult bindingResult = e.getBindingResult();

        List<ErrorVCResponse> response = new ArrayList<>();
        for (FieldError err : bindingResult.getFieldErrors()) {
            logger.info("{}", err.getDefaultMessage());
            response.add(new ErrorVCResponse(errorCode.getCode(), err.getDefaultMessage()));
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<List<ErrorVCResponse>> handleCustomException(CustomException e) {
        BasicErrorCode errorCode = e.getErrorCode();
        List<ErrorVCResponse> response = new ArrayList<>();
        response.add(new ErrorVCResponse(errorCode.getCode(), e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<List<ErrorVCResponse>> handleValidationException(ValidationException e) {
        BasicErrorCode errorCode = e.getErrorCode();
        List<ErrorVCResponse> response = new ArrayList<>();
        response.add(new ErrorVCResponse(errorCode.getCode(), e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResult> testing(NullPointerException e){
        ErrorResult errorResult=new ErrorResult("EMAIL",e.getMessage());
        return new ResponseEntity<>(errorResult,HttpStatus.BAD_REQUEST);
    }*/


//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
//        log.warn("handleIllegalArgument", e);
//        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
//        return handleExceptionInternal(errorCode, e.getMessage());
//    }
//
//    @Override
//    public ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException e,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//        log.warn("handleIllegalArgument", e);
//        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
//        return handleExceptionInternal(e, errorCode);
//    }
//
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> handleAllException(Exception ex) {
//        log.warn("handleAllException", ex);
//        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
//        return handleExceptionInternal(errorCode);
//    }
//
//    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
//        return ResponseEntity.status(errorCode.getHttpStatus())
//                .body(makeErrorResponse(errorCode));
//    }
//
//    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
//        return ErrorResponse.builder()
//                .code(errorCode.name())
//                .message(errorCode.getMessage())
//                .build();
//    }
//
//    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
//        return ResponseEntity.status(errorCode.getHttpStatus())
//                .body(makeErrorResponse(errorCode, message));
//    }
//
//    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
//        return ErrorResponse.builder()
//                .code(errorCode.name())
//                .message(message)
//                .build();
//    }
//
//    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
//        return ResponseEntity.status(errorCode.getHttpStatus())
//                .body(makeErrorResponse(e, errorCode));
//    }
//
//    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
//        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(ErrorResponse.ValidationError::of)
//                .collect(Collectors.toList());
//
//        return ErrorResponse.builder()
//                .code(errorCode.name())
//                .message(errorCode.getMessage())
//                .errors(validationErrorList)
//                .build();
//    }
}
