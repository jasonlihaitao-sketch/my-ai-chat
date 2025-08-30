package com.example.service.impl;

import com.example.request.ChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatSessionService {

    @Autowired
    private TongYiClient tongYiClient;

    private final Map<String, List<ChatRequest.Message>> sessions = new ConcurrentHashMap<>();

    public Flux<String> streamChat(String sessionId, String userMessage, String context) {
        List<ChatRequest.Message> messages = sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // 添加上下文（可选）
        if (context != null && !context.trim().isEmpty()) {
            messages.add(new ChatRequest.Message("system", context));
        }

        // 添加用户消息
        messages.add(new ChatRequest.Message("user", userMessage));

        // 调用流式API
        return tongYiClient.streamChat(messages)
                .doOnComplete(() -> {
                    // 对话完成后添加助手回复到会话历史
                    messages.add(new ChatRequest.Message("assistant", "生成的回复内容"));
                });
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
}