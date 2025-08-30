package com.example.repository;


import com.example.entity.MessageFeedback;
import com.example.repository.MessageFeedbackRepository;
import com.example.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private MessageFeedbackRepository feedbackRepository;

    @Override
    @Transactional
    public MessageFeedback addFeedback(String msgId, String userId, Integer feedbackType, String comment) {
        // 检查是否已经存在反馈
        Optional<MessageFeedback> existingFeedback = feedbackRepository.findByMsgIdAndUserId(msgId, userId);
        if (existingFeedback.isPresent()) {
            // 如果已存在，更新现有反馈
            MessageFeedback feedback = existingFeedback.get();
            feedback.setFeedbackType(feedbackType);
            feedback.setComment(comment);
            return feedbackRepository.save(feedback);
        }

        // 创建新反馈
        MessageFeedback feedback = new MessageFeedback();
        feedback.setMsgId(msgId);
        feedback.setUserId(userId);
        feedback.setFeedbackType(feedbackType);
        feedback.setComment(comment);

        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public MessageFeedback updateFeedback(Long feedbackId, Integer feedbackType, String comment) {
        MessageFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setFeedbackType(feedbackType);
        feedback.setComment(comment);

        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void removeFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    @Override
    public MessageFeedback getFeedbackByMsgAndUser(String msgId, String userId) {
        return feedbackRepository.findByMsgIdAndUserId(msgId, userId)
                .orElse(null);
    }
}