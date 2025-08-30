package com.example.response;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private String requestId;
    private Map<String, Object> details;

    @Data
    public static class ApiError {
        private Error error;

        @Data
        public static class Error {
            private String code;
            private String message;
            private Map<String, Object> details;
        }
    }
}