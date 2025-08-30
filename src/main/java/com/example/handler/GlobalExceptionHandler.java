package com.example.handler;

import com.example.enums.ErrorCode;
import com.example.exception.TongYiException;
import com.example.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TongYiException.class)
    public ResponseEntity<ErrorResponse> handleTongYiException(TongYiException e) {
        log.error("通义大模型异常: code={}, message={}, requestId={}",
                e.getErrorCode(), e.getMessage(), e.getRequestId(), e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(e.getErrorCode());
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setRequestId(e.getRequestId());
        errorResponse.setDetails(e.getDetails());

        HttpStatus status = getHttpStatus(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("系统异常", e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("SystemError");
        errorResponse.setErrorMessage("系统内部错误");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus getHttpStatus(String errorCode) {
        ErrorCode code = ErrorCode.fromCode(errorCode);
        return HttpStatus.valueOf(code.getHttpStatus());
    }
}