package com.jitplus.loyalty.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jitplus.loyalty.dto.RedemptionRequest;
import com.jitplus.loyalty.dto.VisitRequest;
import com.jitplus.loyalty.dto.VisitResponse;
import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.model.ProgramType;
import com.jitplus.loyalty.model.Visit;
import com.jitplus.loyalty.repository.LoyaltyCardRepository;
import com.jitplus.loyalty.repository.LoyaltyProgramRepository;
import com.jitplus.loyalty.repository.VisitRepository;

@Service
public class LoyaltyService {

    private final LoyaltyProgramRepository programRepository;
    private final LoyaltyCardRepository cardRepository;
    private final VisitRepository visitRepository;

    public LoyaltyService(LoyaltyProgramRepository programRepository,
                         LoyaltyCardRepository cardRepository,
                         VisitRepository visitRepository) {
        this.programRepository = programRepository;
        this.cardRepository = cardRepository;
        this.visitRepository = visitRepository;
    }

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

        // Happy Hour Logic
        double multiplier = 1.0;
        if (program.isHappyHourEnabled() && program.getHappyHourStart() != null && program.getHappyHourEnd() != null) {
            try {
                LocalTime now = LocalTime.now();
                LocalTime start = LocalTime.parse(program.getHappyHourStart());
                LocalTime end = LocalTime.parse(program.getHappyHourEnd());
                if (now.isAfter(start) && now.isBefore(end)) {
                    multiplier = program.getHappyHourMultiplier();
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        if (program.getType() == ProgramType.STAMPS) {
            stampsToAdd = (int) (quantity * program.getPointsPerVisit() * multiplier);
            card.setCurrentStamps(card.getCurrentStamps() + stampsToAdd);
        } else if (program.getType() == ProgramType.POINTS) {
            pointsToAdd = (int) (quantity * program.getPointsPerVisit() * multiplier);
            card.setCurrentPoints(card.getCurrentPoints() + pointsToAdd);
        } else if (program.getType() == ProgramType.PROGRESSIVE) {
            // Progressive Logic
            int basePoints = (int) (quantity * program.getPointsPerVisit() * multiplier);
            
            int bonus = 0;
            if (program.getProgressiveStep() > 0) {
                // Calculate level based on total visits (including this one roughly, or previous)
                // User example: 5th passage = 2 points.
                // If step=5, bonus=1.
                // Visit 0 (1st): 0/5 = 0 -> 1 pt
                // Visit 4 (5th): 4/5 = 0? No, user said 5th passage.
                // Let's use (totalVisits + 1) / step
                int level = (card.getTotalVisits()) / program.getProgressiveStep(); 
                bonus = level * program.getProgressiveBonus();
            }
            
            pointsToAdd = basePoints + bonus;
            card.setCurrentPoints(card.getCurrentPoints() + pointsToAdd);
        }
        
        // Increment total visits
        card.setTotalVisits(card.getTotalVisits() + 1);

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
        
        // Increment redemption count
        card.setRedemptionCount(card.getRedemptionCount() + 1);

        // 4. Save and return
        return cardRepository.save(card);
    }
}
