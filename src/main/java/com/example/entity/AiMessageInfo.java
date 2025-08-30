package com.example.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ai_message_info", schema = "mps")
public class AiMessageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "conversation_id", nullable = false, length = 40)
    private String conversationId;

    @Column(name = "msg_id", nullable = false, length = 64)
    private String msgId;

    @Column(name = "application_code", nullable = true, length = 20)
    private String applicationCode;

    @Column(name = "msg_content", columnDefinition = "mediumtext")
    private String msgContent;

    @Column(name = "show_flag", nullable = false, columnDefinition = "int default 0")
    private Integer showFlag;

    @Column(name = "sender_type", nullable = false, columnDefinition = "int default 0")
    private Integer senderType;

    @Column(name = "like_flag", nullable = false, columnDefinition = "int default 0")
    private Integer likeFlag;

    @Column(name = "delete_flag", nullable = false, columnDefinition = "int default 0")
    private Integer deleteFlag;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}