package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ai_feedback")
public class MessageFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "msg_id", nullable = false, length = 64)
    private String msgId;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "feedback_type", nullable = false)
    private Integer feedbackType; // 1:点赞, 2:点踩

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}