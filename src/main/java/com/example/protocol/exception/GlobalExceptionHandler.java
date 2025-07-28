package com.example.protocol.exception;

import com.example.protocol.dto.MessageResponse;
import com.example.protocol.protocol.exception.ProtocolException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("参数校验失败: {}", errors);
        return ResponseEntity.badRequest()
                .body(MessageResponse.error("参数校验失败", errors));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponse> handleConstraintViolationException(
            ConstraintViolationException ex) {

        log.warn("约束违反异常: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(MessageResponse.error("参数约束违反: " + ex.getMessage()));
    }

    /**
     * 处理协议异常
     */
    @ExceptionHandler(ProtocolException.class)
    public ResponseEntity<MessageResponse> handleProtocolException(ProtocolException ex) {
        log.error("协议异常: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(MessageResponse.error("协议处理异常: " + ex.getMessage()));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception ex) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("系统内部错误，请稍后再试"));
    }
}
