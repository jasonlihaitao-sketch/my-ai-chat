package com.example.handler;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ErrorHandler {

    private static final Set<String> RETRYABLE_ERRORS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "RateLimit", "InternalError", "ServiceUnavailable", "ModelOverload"
    )));

    private static final Set<String> CLIENT_ERRORS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "InvalidParameter", "ContentFilter", "ContextLengthExceeded"
    )));

    public boolean shouldRetry(String errorCode) {
        return RETRYABLE_ERRORS.contains(errorCode);
    }

    public boolean isClientError(String errorCode) {
        return CLIENT_ERRORS.contains(errorCode);
    }

    public String getFriendlyMessage(String errorCode) {
        switch (errorCode) {
            case "RateLimit": return "请求过于频繁，请稍后再试";
            case "ContentFilter": return "内容包含敏感信息，请重新输入";
            case "ContextLengthExceeded": return "对话内容过长，请简化问题";
            case "InvalidParameter": return "请求参数错误";
            default: return "系统繁忙，请稍后再试";
        }
    }
}