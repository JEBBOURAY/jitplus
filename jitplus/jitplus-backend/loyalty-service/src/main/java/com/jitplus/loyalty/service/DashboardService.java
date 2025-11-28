package com.jitplus.loyalty.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jitplus.loyalty.dto.DashboardStats;
import com.jitplus.loyalty.dto.TopCustomerDTO;
import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.repository.LoyaltyCardRepository;
import com.jitplus.loyalty.repository.VisitRepository;

@Service
public class DashboardService {
    
    private final LoyaltyCardRepository cardRepository;
    private final VisitRepository visitRepository;
    
    public DashboardService(LoyaltyCardRepository cardRepository, VisitRepository visitRepository) {
        this.cardRepository = cardRepository;
        this.visitRepository = visitRepository;
    }
    
    public DashboardStats getMerchantStats(String merchantId) {
        // Total customers (loyalty cards) for this merchant
        long totalCustomers = cardRepository.countByMerchantId(merchantId);
        
        // Visits this week (from Monday 00:00)
        LocalDateTime startOfWeek = LocalDateTime.now()
            .truncatedTo(ChronoUnit.DAYS)
            .with(java.time.DayOfWeek.MONDAY)
            .withHour(0).withMinute(0).withSecond(0);
        long visitsThisWeek = visitRepository.countVisitsByMerchantIdSince(merchantId, startOfWeek);
        
        // Total rewards distributed
        Long totalRewards = cardRepository.sumRedemptionCountByMerchantId(merchantId);
        long totalRewardsDistributed = (totalRewards != null) ? totalRewards : 0L;
        
        // Top 10 Customers
        List<LoyaltyCard> topCards = cardRepository.findTop10ByMerchantIdOrderByTotalVisitsDesc(merchantId);
        List<TopCustomerDTO> topCustomers = topCards.stream()
            .map(card -> new TopCustomerDTO(
                card.getCustomerName() != null ? card.getCustomerName() : "Client #" + card.getCustomerId(),
                card.getCustomerPhone() != null ? card.getCustomerPhone() : "N/A",
                card.getTotalVisits(),
                card.getRedemptionCount()
            ))
            .collect(Collectors.toList());
        
        return new DashboardStats(totalCustomers, visitsThisWeek, totalRewardsDistributed, topCustomers);
    }
}
