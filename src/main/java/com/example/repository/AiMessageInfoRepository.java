package com.example.repository;

import com.example.entity.AiMessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiMessageInfoRepository extends JpaRepository<AiMessageInfo, Long> {
    List<AiMessageInfo> findMessagesByConversationId(String conversationId);

    @Transactional
    @Modifying
    @Query("UPDATE AiMessageInfo m SET m.msgContent = :msgContent WHERE m.id = :id")
    void updateMessageContent(@Param("id") Long id, @Param("msgContent") String msgContent);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiMessageInfo m WHERE m.id = ?1")
    void deleteMessageById(Long id);

    // 添加 findMessageById 方法
    Optional<AiMessageInfo> findMessageById(Long id);
}