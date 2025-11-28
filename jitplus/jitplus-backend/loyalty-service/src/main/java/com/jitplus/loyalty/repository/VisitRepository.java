package com.jitplus.loyalty.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jitplus.loyalty.model.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    
    @Query("SELECT COUNT(v) FROM Visit v WHERE v.cardId IN (SELECT lc.id FROM LoyaltyCard lc WHERE lc.merchantId = :merchantId) AND v.timestamp >= :startDate")
    long countVisitsByMerchantIdSince(@Param("merchantId") String merchantId, @Param("startDate") LocalDateTime startDate);
}
