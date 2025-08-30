package com.example.service;

import com.example.entity.AiMessageInfo;
import com.example.entity.Conversation;
import com.example.enums.MessageRole;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConversationService {
    Conversation createConversation(String userId, String title, String modelCode);
    List<Conversation> getUserConversations(String userId);
    Conversation getConversation(String conversationId);
    Conversation updateConversationTitle(String conversationId, String title);
    Conversation updateConversationModel(String conversationId, String modelCode);
    void deleteConversation(String conversationId);
    void permanentlyDeleteConversation(String conversationId);

    public Mono<Void> saveMessage(String userId, String conversationId, String generateMsgId, Integer role,
                                  String msgContent);
    List<AiMessageInfo> getMsgList(String conversationId);
    public String generateContextId();
    public String generateMsgId();
}