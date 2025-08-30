package com.example.service.impl;

import com.example.exception.TongYiException;
import com.example.request.ChatRequest;
import com.example.response.ChatResponse;
import com.example.response.ErrorResponse;
import com.example.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.util.retry.Retry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class TongYiClient {

    private final OkHttpClient httpClient;
    private final String endpoint;
    private final String apiKey;

    public Flux<String> streamChat(List<ChatRequest.Message> messages) {
        return Flux.create(emitter -> {
            try {
                ChatRequest request = new ChatRequest();
                request.setMessages(messages);

                Request httpRequest = buildRequest(request);

                httpClient.newCall(httpRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handleNetworkError(e, emitter);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (!response.isSuccessful()) {
                                handleErrorResponse(response, emitter);
                                return;
                            }

                            processStreamResponse(response, emitter);

                        } catch (Exception e) {
                            emitter.error(new TongYiException("StreamError", "流处理错误: " + e.getMessage(),
                                    response.header("X-Request-Id")));
                        } finally {
                            response.close();
                        }
                    }
                });

            } catch (Exception e) {
                emitter.error(new TongYiException("RequestError", "请求构建错误: " + e.getMessage(), null));
            }
        });
    }

    public Request buildRequest(ChatRequest request) throws JsonProcessingException {
        return new Request.Builder()
                .url(endpoint )
                .post(RequestBody.create(
                        JsonUtils.toJson(request),
                        MediaType.get("application/json")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Request-Id", UUID.randomUUID().toString())
                .build();
    }

    private void processStreamResponse(Response response, FluxSink<String> emitter) throws IOException {
        try (ResponseBody body = response.body();
             BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()))) {

            String line;
            while ((line = reader.readLine()) != null && !emitter.isCancelled()) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    if ("[DONE]".equals(data)) {
                        emitter.complete();
                        break;
                    }

                    try {
                        // 检查是否是错误响应
                        if (data.contains("\"error\"")) {
                            handleStreamError(data, emitter, response.header("X-Request-Id"));
                            break;
                        }

                        ChatResponse responseObj = JsonUtils.fromJson(data, ChatResponse.class);
                        if (responseObj != null && responseObj.getChoices() != null) {
                            responseObj.getChoices().forEach(choice -> {
                                if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
                                    emitter.next(choice.getDelta().getContent());
                                }
                            });
                        }
                    } catch (Exception e) {
                        log.warn("解析流式响应失败: {}", data, e);
                    }
                }
            }
        }
    }

    private void handleNetworkError(IOException e, FluxSink<String> emitter) {
        log.error("网络请求失败", e);
        emitter.error(new TongYiException("NetworkError", "网络连接错误: " + e.getMessage(), null));
    }

    private void handleErrorResponse(Response response, FluxSink<String> emitter) throws IOException {
        String requestId = response.header("X-Request-Id");
        String responseBody = response.body().string();

        log.error("API请求失败: status={}, requestId={}, body={}",
                response.code(), requestId, responseBody);

        try {
            ErrorResponse.ApiError apiError = JsonUtils.fromJson(responseBody, ErrorResponse.ApiError.class);
            if (apiError != null && apiError.getError() != null) {
                throw createExceptionFromError(apiError.getError(), requestId);
            }
        } catch (Exception e) {
            // 如果无法解析错误响应，使用HTTP状态码
            throw createExceptionFromStatusCode(response.code(), responseBody, requestId);
        }
    }

    private void handleStreamError(String errorData, FluxSink<String> emitter, String requestId) {
        try {
            ErrorResponse.ApiError apiError = JsonUtils.fromJson(errorData, ErrorResponse.ApiError.class);
            if (apiError != null && apiError.getError() != null) {
                emitter.error(createExceptionFromError(apiError.getError(), requestId));
            }
        } catch (Exception e) {
            emitter.error(new TongYiException("StreamError", "流式响应中的错误: " + errorData, requestId));
        }
    }

    private TongYiException createExceptionFromError(ErrorResponse.ApiError.Error error, String requestId) {
        String errorCode = error.getCode();
        String errorMsg = error.getMessage();

        switch (errorCode) {
            case "RateLimit":
                return new TongYiException.RateLimitException(errorMsg, requestId);
            case "InvalidParameter":
                return new TongYiException.InvalidParameterException(errorMsg, requestId);
            case "InternalError":
                return new TongYiException.InternalErrorException(errorMsg, requestId);
            case "ContentFilter":
                return new TongYiException("ContentFilter", "内容安全检测不通过: " + errorMsg, requestId, error.getDetails());
            case "ContextLengthExceeded":
                return new TongYiException("ContextLengthExceeded", "上下文长度超限: " + errorMsg, requestId);
            default:
                return new TongYiException(errorCode, errorMsg, requestId, error.getDetails());
        }
    }

    private TongYiException createExceptionFromStatusCode(int statusCode, String responseBody, String requestId) {
        switch (statusCode) {
            case 400:
                return new TongYiException("BadRequest", "请求格式错误: " + responseBody, requestId);
            case 401:
                return new TongYiException("Unauthorized", "认证失败", requestId);
            case 403:
                return new TongYiException("Forbidden", "权限不足", requestId);
            case 429:
                return new TongYiException.RateLimitException("请求频率超限", requestId);
            case 500:
                return new TongYiException.InternalErrorException("服务内部错误", requestId);
            case 503:
                return new TongYiException("ServiceUnavailable", "服务不可用", requestId);
            default:
                return new TongYiException("UnknownError", "未知错误: " + statusCode + " - " + responseBody, requestId);
        }
    }

    // 重试机制（可选）
    // 定义一个带有重试机制的流式聊天方法
    public Flux<String> streamChatWithRetry(List<ChatRequest.Message> messages, int maxRetries) {
        // 使用 Flux.defer 延迟创建流，确保每次订阅时重新调用 streamChat(messages)
        return Flux.defer(() -> streamChat(messages))
                // 配置重试策略
                .retryWhen(
                        // 使用指数退避算法实现重试
                        Retry.backoff(maxRetries, Duration.ofSeconds(1))
                                // 过滤可重试的异常
                                .filter(throwable -> isRetryableException(throwable))
                );
    }

    // 判断异常是否可重试
    private boolean isRetryableException(Throwable throwable) {
        // 如果是 TongYiException 类型
        if (throwable instanceof TongYiException) {
            TongYiException ex = (TongYiException) throwable;
            // 以下异常不可重试：
            // 1. 参数错误（InvalidParameter）
            // 2. 内容安全检测不通过（ContentFilter）
            // 3. 上下文长度超限（ContextLengthExceeded）
            return !ex.getErrorCode().equals("InvalidParameter") &&
                    !ex.getErrorCode().equals("ContentFilter") &&
                    !ex.getErrorCode().equals("ContextLengthExceeded");
        }
        // 其他异常（如网络错误）默认可重试
        return true;
    }

}