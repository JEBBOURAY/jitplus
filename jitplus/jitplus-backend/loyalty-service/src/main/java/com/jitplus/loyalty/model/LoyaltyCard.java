package com.jitplus.loyalty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loyalty_cards")
public class LoyaltyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private Long customerId;
    
    private String customerName;
    private String customerPhone;

    private int currentPoints; // or stamps
    private int currentStamps;
    private int redemptionCount = 0; // Track total rewards redeemed
    private int totalVisits = 0; // Track total visits for loyalty ranking

    public LoyaltyCard() {
    }

    public LoyaltyCard(String merchantId, Long customerId, String customerName, String customerPhone) {
        this.merchantId = merchantId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.currentPoints = 0;
        this.currentStamps = 0;
        this.totalVisits = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public int getCurrentStamps() {
        return currentStamps;
    }

    public void setCurrentStamps(int currentStamps) {
        this.currentStamps = currentStamps;
    }
    
    public int getRedemptionCount() {
        return redemptionCount;
    }

    public void setRedemptionCount(int redemptionCount) {
        this.redemptionCount = redemptionCount;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }
}
