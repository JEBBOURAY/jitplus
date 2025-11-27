package com.jitplus.merchant.data.model

data class VisitRequest(
    val merchantId: String,
    val customerId: Long,
    val quantity: Int
)

data class VisitResponse(
    val cardId: Long,
    val currentStamps: Int,
    val currentPoints: Int,
    val rewardUnlocked: Boolean,
    val rewardDescription: String
)
