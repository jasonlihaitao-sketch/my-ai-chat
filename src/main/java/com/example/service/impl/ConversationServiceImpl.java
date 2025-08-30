package com.example.service.impl;

import com.example.entity.AiMessageInfo;
import com.example.entity.Conversation;
import com.example.enums.MessageRole;
import com.example.repository.AiMessageInfoRepository;
import com.example.repository.ConversationRepository;
import com.example.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private AiMessageInfoRepository aiMessageInfoRepository;

    @Override
    public Conversation createConversation(String userId, String title, String modelCode) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title != null ? title : "新对话");
        conversation.setModelCode(modelCode != null ? modelCode : "qwen-turbo");
        return conversationRepository.save(conversation);
    }

    @Override
    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByUserIdAndDeleteFlagOrderByUpdateTimeDesc(userId, 0);
    }

    @Override
    public Conversation getConversation(String conversationId) {
        Optional<Conversation> conversation = conversationRepository.findByConversationIdAndDeleteFlag(conversationId, 0);
        return conversation.orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    @Override
    @Transactional
    public Conversation updateConversationTitle(String conversationId, String title) {
        Conversation conversation = getConversation(conversationId);
        conversation.setTitle(title);
        return conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public Conversation updateConversationModel(String conversationId, String modelCode) {
        Conversation conversation = getConversation(conversationId);
        conversation.setModelCode(modelCode);
        return conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        conversationRepository.softDeleteByConversationId(conversationId);
    }

    @Override
    @Transactional
    public void permanentlyDeleteConversation(String conversationId) {
        conversationRepository.deleteByConversationId(conversationId);
    }

    @Override
    public Mono<Void> saveMessage(String userId, String conversationId,String generateMsgId,Integer role,
                                     String msgContent) {
        AiMessageInfo message = new AiMessageInfo();
        message.setUserId(userId);
        message.setConversationId(conversationId);
        message.setMsgId(generateMsgId);
        message.setApplicationCode("1000");
        message.setMsgContent(msgContent);
        message.setSenderType(role);
        message.setDeleteFlag(0);
        message.setLikeFlag(0);
        message.setShowFlag(0);
        return Mono.fromRunnable(() -> aiMessageInfoRepository.save(message));
    }

    @Override
    public List<AiMessageInfo> getMsgList(String conversationId) {
        List<AiMessageInfo> aiMessageInfos = aiMessageInfoRepository.findMessagesByConversationId(conversationId);
        return aiMessageInfos;
    }

    @Override
    public String generateContextId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateMsgId() {
        return UUID.randomUUID().toString();
    }

}