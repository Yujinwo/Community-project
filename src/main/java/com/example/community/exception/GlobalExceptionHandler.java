package com.example.community.exception;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;

// 모든 컨트롤러에서 발생하는 예외를 처리하는 전역 예외 처리기
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus
    public Map<String,String> handleValidationExceptions (MethodArgumentNotValidException ex) {
        // 에러 메시지를 저장할 Map 생성
        Map<String,String> errors = new HashMap<>();
        // MethodArgumentNotValidException에서 발생한 모든 오류에 대해 반복
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // 오류가 발생한 필드 이름
            String fieldName = ((FieldError) error).getField();
            // 오류 메시지
            String errorMessage = error.getDefaultMessage();
            // Map에 필드 이름과 오류 메시지 저장
            errors.put(fieldName,errorMessage);

        });
        // 저장된 모든 오류 메시지를 반환
        return errors;
    }
}
