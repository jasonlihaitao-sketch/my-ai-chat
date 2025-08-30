package com.example.service;

import com.example.entity.AiMessageInfo;
import  com.example.repository.AiMessageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AiMessageInfoService {

    @Autowired
    private AiMessageInfoRepository messageRepository;

    /**
     * 创建消息
     * @param message 消息实体
     * @return 保存后的消息
     */
    public AiMessageInfo createMessage(AiMessageInfo message) {
        return messageRepository.save(message);
    }

    /**
     * 根据消息ID查询消息
     * @param id 消息ID
     * @return 消息实体
     */
    public Optional<AiMessageInfo> getMessageById(Long id) {
        return messageRepository.findMessageById(id);
    }

    /**
     * 根据会话ID查询消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    public List<AiMessageInfo> getMessagesByConversationId(String conversationId) {
        return messageRepository.findMessagesByConversationId(conversationId);
    }

    /**
     * 更新消息内容
     * @param id 消息ID
     * @param content 新内容
     */
    public void updateMessageContent(Long id, String content) {
        messageRepository.updateMessageContent(id, content);
    }

    /**
     * 删除消息
     * @param id 消息ID
     */
    public void deleteMessage(Long id) {
        messageRepository.deleteMessageById(id);
    }

    /**
     * 检查消息是否存在
     * @param id 消息ID
     * @return 是否存在
     */
    public boolean existsById(Long id) {
        return messageRepository.existsById(id);
    }
}
