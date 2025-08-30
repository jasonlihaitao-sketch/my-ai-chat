package com.example.service;

import com.example.dto.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface StreamResponseService {
    /**
     * 流式聊天响应
     * @param request 聊天请求
     * @param conversationId 会话ID
     * @return SSE 发射器
     */
    SseEmitter streamChat(ChatRequest request, String conversationId);
}