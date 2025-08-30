package com.example.service;


import com.example.entity.MessageFeedback;

public interface FeedbackService {
    MessageFeedback addFeedback(String msgId, String userId, Integer feedbackType, String comment);
    MessageFeedback updateFeedback(Long feedbackId, Integer feedbackType, String comment);
    void removeFeedback(Long feedbackId);
    MessageFeedback getFeedbackByMsgAndUser(String msgId, String userId);
}