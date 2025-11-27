package com.jitplus.loyalty.repository;

import com.jitplus.loyalty.model.LoyaltyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCard, Long> {
    Optional<LoyaltyCard> findByMerchantIdAndCustomerId(String merchantId, Long customerId);
}
