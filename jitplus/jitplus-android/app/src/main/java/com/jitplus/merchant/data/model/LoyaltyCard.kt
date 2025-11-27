package com.jitplus.merchant.data.model

data class LoyaltyCard(
    val id: Long,
    val merchantId: String,
    val customerId: Long,
    val currentPoints: Int,
    val currentStamps: Int
)
