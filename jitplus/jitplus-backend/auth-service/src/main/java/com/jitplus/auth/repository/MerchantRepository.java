package com.jitplus.auth.repository;

import com.jitplus.auth.model.MerchantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantUser, Long> {
    Optional<MerchantUser> findByEmail(String email);
}
