package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ai_token_usage")
public class TokenUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "conversation_id", nullable = false, length = 40)
    private String conversationId;

    @Column(name = "model_code", nullable = false, length = 50)
    private String modelCode;

    @Column(name = "token_count", nullable = false)
    private Integer tokenCount = 0;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (usageDate == null) {
            usageDate = LocalDate.now();
        }
    }
}