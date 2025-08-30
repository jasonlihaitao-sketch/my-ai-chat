package com.example.repository;

import com.example.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {
    Optional<MessageFeedback> findByMsgIdAndUserId(String msgId, String userId);
    boolean existsByMsgIdAndUserId(String msgId, String userId);
}