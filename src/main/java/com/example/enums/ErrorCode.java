package com.example.enums;

import lombok.Data;

public enum ErrorCode {

    // 客户端错误 (4xx)
    INVALID_PARAMETER("InvalidParameter", "请求参数错误", 400),
    UNAUTHORIZED("Unauthorized", "认证失败", 401),
    FORBIDDEN("Forbidden", "权限不足", 403),
    NOT_FOUND("NotFound", "资源不存在", 404),
    RATE_LIMIT("RateLimit", "请求频率超限", 429),

    // 服务端错误 (5xx)
    INTERNAL_ERROR("InternalError", "服务内部错误", 500),
    SERVICE_UNAVAILABLE("ServiceUnavailable", "服务不可用", 503),
    MODEL_OVERLOAD("ModelOverload", "模型过载", 503),

    // 业务错误
    CONTENT_FILTER("ContentFilter", "内容安全检测不通过", 400),
    CONTEXT_LENGTH_EXCEEDED("ContextLengthExceeded", "上下文长度超限", 400),
    MODEL_NOT_SUPPORTED("ModelNotSupported", "模型不支持", 400);

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}