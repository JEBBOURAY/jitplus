package com.jitplus.loyalty.model;

import jakarta.persistence.*;

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

    private int currentPoints; // or stamps
    private int currentStamps;

    public LoyaltyCard() {
    }

    public LoyaltyCard(String merchantId, Long customerId) {
        this.merchantId = merchantId;
        this.customerId = customerId;
        this.currentPoints = 0;
        this.currentStamps = 0;
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
}
