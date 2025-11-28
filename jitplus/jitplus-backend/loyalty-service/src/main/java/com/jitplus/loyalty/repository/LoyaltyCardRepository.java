package com.jitplus.loyalty.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jitplus.loyalty.model.LoyaltyCard;

@Repository
public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCard, Long> {
    Optional<LoyaltyCard> findByMerchantIdAndCustomerId(String merchantId, Long customerId);
    long countByMerchantId(String merchantId);
    
    @Query("SELECT SUM(lc.redemptionCount) FROM LoyaltyCard lc WHERE lc.merchantId = :merchantId")
    Long sumRedemptionCountByMerchantId(@Param("merchantId") String merchantId);
    
    List<LoyaltyCard> findTop10ByMerchantIdOrderByTotalVisitsDesc(String merchantId);
}
