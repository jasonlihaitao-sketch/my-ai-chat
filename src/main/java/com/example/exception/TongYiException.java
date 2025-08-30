package com.example.exception;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TongYiException extends RuntimeException {
    private final String errorCode;
    private final String requestId;
    private final Map<String, Object> details;

    public TongYiException(String errorCode, String message, String requestId) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.details = new HashMap<>();
    }

    public TongYiException(String errorCode, String message, String requestId, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.details = details != null ? details : new HashMap<>();
    }

    // 常用错误异常类
    public static class RateLimitException extends TongYiException {
        public RateLimitException(String message, String requestId) {
            super("RateLimit", message, requestId);
        }
    }

    public static class InvalidParameterException extends TongYiException {
        public InvalidParameterException(String message, String requestId) {
            super("InvalidParameter", message, requestId);
        }
    }

    public static class InternalErrorException extends TongYiException {
        public InternalErrorException(String message, String requestId) {
            super("InternalError", message, requestId);
        }
    }


}