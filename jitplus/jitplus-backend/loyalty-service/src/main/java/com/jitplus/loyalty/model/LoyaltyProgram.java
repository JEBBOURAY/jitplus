package com.jitplus.loyalty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "loyalty_programs")
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Merchant ID is required")
    private String merchantId; // Using email/username as ID for MVP

    @NotBlank(message = "Program name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Program type is required")
    private ProgramType type;

    @Min(value = 1, message = "Points per visit must be at least 1")
    private int pointsPerVisit; // Or stamps per visit
    
    @Min(value = 1, message = "Threshold must be at least 1")
    private int threshold; // Points/Stamps needed for reward
    
    @NotBlank(message = "Reward description is required")
    private String rewardDescription;

    public LoyaltyProgram() {
    }

    public LoyaltyProgram(String merchantId, String name, ProgramType type, int pointsPerVisit, int threshold, String rewardDescription) {
        this.merchantId = merchantId;
        this.name = name;
        this.type = type;
        this.pointsPerVisit = pointsPerVisit;
        this.threshold = threshold;
        this.rewardDescription = rewardDescription;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    public int getPointsPerVisit() {
        return pointsPerVisit;
    }

    public void setPointsPerVisit(int pointsPerVisit) {
        this.pointsPerVisit = pointsPerVisit;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }
}
