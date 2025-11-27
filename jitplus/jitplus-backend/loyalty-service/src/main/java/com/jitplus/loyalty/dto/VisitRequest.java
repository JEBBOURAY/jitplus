package com.jitplus.loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VisitRequest {
    @NotBlank(message = "Merchant ID is required")
    private String merchantId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
