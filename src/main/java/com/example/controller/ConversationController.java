package com.example.controller;

import com.example.entity.Conversation;
import com.example.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public Conversation createConversation(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String modelCode) {
        return conversationService.createConversation(userId, title, modelCode);
    }

    @GetMapping
    public List<Conversation> getUserConversations(@AuthenticationPrincipal String userId) {
        return conversationService.getUserConversations(userId);
    }

    @GetMapping("/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId) {
        return conversationService.getConversation(conversationId);
    }

    @PutMapping("/{conversationId}/title")
    public Conversation updateConversationTitle(
            @PathVariable String conversationId,
            @RequestParam String title) {
        return conversationService.updateConversationTitle(conversationId, title);
    }

    @PutMapping("/{conversationId}/model")
    public Conversation updateConversationModel(
            @PathVariable String conversationId,
            @RequestParam String modelCode) {
        return conversationService.updateConversationModel(conversationId, modelCode);
    }

    @DeleteMapping("/{conversationId}")
    public void deleteConversation(@PathVariable String conversationId) {
        conversationService.deleteConversation(conversationId);
    }
}