package com.jitplus.loyalty.service;

import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.repository.LoyaltyProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoyaltyProgramService {

    @Autowired
    private LoyaltyProgramRepository repository;

    public LoyaltyProgram createOrUpdateProgram(LoyaltyProgram program) {
        Optional<LoyaltyProgram> existing = repository.findByMerchantId(program.getMerchantId());
        if (existing.isPresent()) {
            LoyaltyProgram p = existing.get();
            p.setName(program.getName());
            p.setType(program.getType());
            p.setPointsPerVisit(program.getPointsPerVisit());
            p.setThreshold(program.getThreshold());
            p.setRewardDescription(program.getRewardDescription());
            return repository.save(p);
        } else {
            return repository.save(program);
        }
    }

    public Optional<LoyaltyProgram> getProgramByMerchantId(String merchantId) {
        return repository.findByMerchantId(merchantId);
    }
}
