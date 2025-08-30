package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ai_conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", unique = true, nullable = false, length = 40)
    private String conversationId;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title = "新对话";

    @Column(name = "model_code", nullable = false, length = 50)
    private String modelCode = "qwen-turbo";

    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens = 0;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag = 0;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        conversationId = "conv_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}