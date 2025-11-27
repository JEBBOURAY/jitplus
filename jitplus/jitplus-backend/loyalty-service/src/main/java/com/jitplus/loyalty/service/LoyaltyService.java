package com.jitplus.loyalty.service;

import com.jitplus.loyalty.dto.VisitRequest;
import com.jitplus.loyalty.dto.VisitResponse;
import com.jitplus.loyalty.dto.RedemptionRequest;
import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.model.ProgramType;
import com.jitplus.loyalty.model.Visit;
import com.jitplus.loyalty.repository.LoyaltyCardRepository;
import com.jitplus.loyalty.repository.LoyaltyProgramRepository;
import com.jitplus.loyalty.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoyaltyService {

    @Autowired
    private LoyaltyProgramRepository programRepository;

    @Autowired
    private LoyaltyCardRepository cardRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Transactional
    public VisitResponse recordVisit(VisitRequest request) {
        // 1. Get Program
        LoyaltyProgram program = programRepository.findByMerchantId(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        // 2. Get Card
        LoyaltyCard card = cardRepository.findByMerchantIdAndCustomerId(request.getMerchantId(), request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // 3. Calculate addition
        int quantity = request.getQuantity();
        int stampsToAdd = 0;
        int pointsToAdd = 0;

        if (program.getType() == ProgramType.STAMPS) {
            stampsToAdd = quantity * program.getPointsPerVisit();
            card.setCurrentStamps(card.getCurrentStamps() + stampsToAdd);
        } else {
            pointsToAdd = quantity * program.getPointsPerVisit();
            card.setCurrentPoints(card.getCurrentPoints() + pointsToAdd);
        }

        // 4. Save Card
        cardRepository.save(card);

        // 5. Create Visit record
        Visit visit = new Visit();
        visit.setCardId(card.getId());
        visit.setQuantity(quantity);
        visit.setTimestamp(LocalDateTime.now());
        visitRepository.save(visit);

        // 6. Check Reward
        boolean rewardUnlocked = false;
        if (program.getType() == ProgramType.STAMPS) {
            rewardUnlocked = card.getCurrentStamps() >= program.getThreshold();
        } else {
            rewardUnlocked = card.getCurrentPoints() >= program.getThreshold();
        }

        return new VisitResponse(card.getId(), card.getCurrentStamps(), card.getCurrentPoints(), rewardUnlocked, program.getRewardDescription());
    }

    @Transactional
    public LoyaltyCard redeemReward(RedemptionRequest request) {
        // 1. Get Program
        LoyaltyProgram program = programRepository.findByMerchantId(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        // 2. Get Card
        LoyaltyCard card = cardRepository.findByMerchantIdAndCustomerId(request.getMerchantId(), request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // 3. Check balance and deduct
        if (program.getType() == ProgramType.STAMPS) {
            if (card.getCurrentStamps() < program.getThreshold()) {
                throw new RuntimeException("Not enough stamps for reward");
            }
            card.setCurrentStamps(card.getCurrentStamps() - program.getThreshold());
        } else {
            if (card.getCurrentPoints() < program.getThreshold()) {
                throw new RuntimeException("Not enough points for reward");
            }
            card.setCurrentPoints(card.getCurrentPoints() - program.getThreshold());
        }

        // 4. Save and return
        return cardRepository.save(card);
    }
}
