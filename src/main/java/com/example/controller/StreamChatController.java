package com.example.controller;


import com.example.dto.ChatRequest;
import com.example.service.StreamResponseService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/chat")
public class StreamChatController {

    @Autowired
    private StreamResponseService streamResponseService;

    @PostMapping("/stream")
    public SseEmitter streamChat(
            @AuthenticationPrincipal String userId,
            @RequestBody ChatRequest request,
            @RequestParam(required = false) String conversationId) {

        // 设置用户ID
        request.setUserId(userId);

        return streamResponseService.streamChat(request, conversationId);
    }
}