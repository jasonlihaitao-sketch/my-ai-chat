package com.example.repository;

import com.example.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserIdAndDeleteFlagOrderByUpdateTimeDesc(String userId, Integer deleteFlag);
    Optional<Conversation> findByConversationIdAndDeleteFlag(String conversationId, Integer deleteFlag);

    @Modifying
    @Query("UPDATE Conversation c SET c.deleteFlag = 1 WHERE c.conversationId = :conversationId")
    void softDeleteByConversationId(@Param("conversationId") String conversationId);

    @Modifying
    @Query("DELETE FROM Conversation c WHERE c.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") String conversationId);
}