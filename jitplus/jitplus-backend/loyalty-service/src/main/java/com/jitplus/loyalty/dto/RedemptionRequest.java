package com.jitplus.loyalty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RedemptionRequest {
    @NotBlank(message = "Merchant ID is required")
    private String merchantId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;

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
}
