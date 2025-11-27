package com.jitplus.loyalty.repository;

import com.jitplus.loyalty.model.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    Optional<LoyaltyProgram> findByMerchantId(String merchantId);
}
