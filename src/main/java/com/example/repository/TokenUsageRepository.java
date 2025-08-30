package com.example.repository;

import com.example.entity.TokenUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenUsageRepository extends JpaRepository<TokenUsage, Long> {
    Optional<TokenUsage> findByUserIdAndUsageDate(String userId, LocalDate usageDate);

    @Query("SELECT SUM(t.tokenCount) FROM TokenUsage t WHERE t.userId = :userId AND t.usageDate = :usageDate")
    Integer sumTokenCountByUserIdAndUsageDate(@Param("userId") String userId, @Param("usageDate") LocalDate usageDate);

    @Query("SELECT t FROM TokenUsage t WHERE t.userId = :userId AND t.usageDate BETWEEN :startDate AND :endDate ORDER BY t.usageDate")
    List<TokenUsage> findByUserIdAndUsageDateBetween(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}