package com.example.controller;


import com.example.enums.MessageRole;
import com.example.exception.TongYiException;
import com.example.request.ChatRequest;
import com.example.service.ConversationService;
import com.example.service.impl.TongYiClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/tychat")
public class ChatController {

    @Autowired
    private TongYiClient tongYiClient;
    @Autowired
    private ConversationService conversationService;

    @PostMapping("/stream")
    public Flux<String> streamChat(@RequestBody ChatStreamRequest request) {
        // 参数验证
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return Flux.error(new TongYiException.InvalidParameterException("消息内容不能为空", null));
        }

        if (request.getMessage().length() > 2000) {
            return Flux.error(new TongYiException.InvalidParameterException("消息长度超限", null));
        }
        String contextId = request.getContextId() != null ? request.getContextId() : conversationService.generateContextId();
// 在请求开始时设置上下文（如Filter或Interceptor中）
        String generateMsgId = conversationService.generateMsgId();
        MDC.put("requestId", generateMsgId);
        MDC.put("userId", "user123");
        // 保存用户消息
        conversationService.saveMessage(request.getUserId(), contextId, generateMsgId, MessageRole.USER.getRole(), request.getMessage()).subscribe();
        List<ChatRequest.Message> messages = buildMessages(request);
        log.info("准备调用通义API - 原始消息: {}, 消息列表: {}", request.getMessage(), messages);

        // 调用通义API并返回流式响应
        return tongYiClient.streamChatWithRetry(messages, 3)
                // 在流式响应开始时打印日志
                .doOnSubscribe(subscription -> {

                })
                // 在每次返回数据给前端之前打印日志
                .doOnNext(data -> {
                    MDC.put("requestId", generateMsgId);
                    MDC.put("userId", "user123");
                    log.info("返回给前端的流式数据: {}", data);
                }) // 收集所有数据块
                .collectList()
                // 保存完整回复到数据库
                .flatMapMany(aiResponses -> {

                    String fullResponse = String.join("", aiResponses);
                    log.info("完整回复内容: {}", fullResponse);

                    // 保存完整回复到数据库
                    return conversationService.saveMessage(
                                    request.getUserId(),
                                    contextId,
                                    generateMsgId,
                                    MessageRole.ASSISTANT.getRole(),
                                    fullResponse
                            )
                            // 继续返回流式数据给前端
                            .thenMany(Flux.fromIterable(aiResponses));
                })
                // 在流式响应完成时打印日志
                .doOnComplete(() -> {
                    log.info("流式响应处理完成");
                    MDC.clear(); // 避免内存泄漏

                })
                // 在发生错误时打印日志
                .doOnError(error -> {
                    log.error("流式响应处理异常", error);
                    MDC.clear(); // 避免内存泄漏

                });
    }

    private List<ChatRequest.Message> buildMessages(ChatStreamRequest request) {
        List<ChatRequest.Message> messages = new ArrayList<>();

        if (request.getContext() != null) {
            messages.add(new ChatRequest.Message("system", request.getContext()));
        }
        messages.add(new ChatRequest.Message("user", request.getMessage()));
        return messages;
    }

    @Data
    public static class ChatStreamRequest {
        private String message;
        private String context; // 可选上下文
        private String model;   // 可选模型
        private String contextId; // 可选上下文
        private String userId; // 可选上下文
    }
}