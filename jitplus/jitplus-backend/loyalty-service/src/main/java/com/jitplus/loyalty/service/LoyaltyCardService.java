package com.jitplus.loyalty.service;

import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.repository.LoyaltyCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoyaltyCardService {

    private final LoyaltyCardRepository repository;

    public LoyaltyCardService(LoyaltyCardRepository repository) {
        this.repository = repository;
    }

    public LoyaltyCard createCardIfNotExists(String merchantId, Long customerId) {
        Optional<LoyaltyCard> existing = repository.findByMerchantIdAndCustomerId(merchantId, customerId);
        if (existing.isPresent()) {
            return existing.get();
        }
        return repository.save(new LoyaltyCard(merchantId, customerId));
    }

    public Optional<LoyaltyCard> getCard(String merchantId, Long customerId) {
        return repository.findByMerchantIdAndCustomerId(merchantId, customerId);
    }
}
