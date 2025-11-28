package com.jitplus.loyalty.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.repository.LoyaltyCardRepository;

@Service
public class LoyaltyCardService {

    private final LoyaltyCardRepository repository;

    public LoyaltyCardService(LoyaltyCardRepository repository) {
        this.repository = repository;
    }

    public LoyaltyCard createCardIfNotExists(String merchantId, Long customerId, String customerName, String customerPhone) {
        Optional<LoyaltyCard> existing = repository.findByMerchantIdAndCustomerId(merchantId, customerId);
        if (existing.isPresent()) {
            return existing.get();
        }
        return repository.save(new LoyaltyCard(merchantId, customerId, customerName, customerPhone));
    }

    public Optional<LoyaltyCard> getCard(String merchantId, Long customerId) {
        return repository.findByMerchantIdAndCustomerId(merchantId, customerId);
    }
}
