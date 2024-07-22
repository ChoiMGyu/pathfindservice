/*
 * 클래스 기능 : 예외 발생했을 때 응답을 처리하는 클래스
 * 최근 수정 일자 : 2024.07.20(토)
 */
package com.pathfind.system.exception;

import com.pathfind.system.memberDto.EmailVCRequest;
import com.pathfind.system.memberDto.NicknameVCRequest;
import com.pathfind.system.memberDto.UserIdVCRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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
}
