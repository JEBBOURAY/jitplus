package com.jitplus.loyalty.dto;

public class VisitResponse {
    private Long cardId;
    private int currentStamps;
    private int currentPoints;
    private boolean rewardUnlocked;
    private String rewardDescription;

    public VisitResponse(Long cardId, int currentStamps, int currentPoints, boolean rewardUnlocked, String rewardDescription) {
        this.cardId = cardId;
        this.currentStamps = currentStamps;
        this.currentPoints = currentPoints;
        this.rewardUnlocked = rewardUnlocked;
        this.rewardDescription = rewardDescription;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public int getCurrentStamps() {
        return currentStamps;
    }

    public void setCurrentStamps(int currentStamps) {
        this.currentStamps = currentStamps;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public boolean isRewardUnlocked() {
        return rewardUnlocked;
    }

    public void setRewardUnlocked(boolean rewardUnlocked) {
        this.rewardUnlocked = rewardUnlocked;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }
}
