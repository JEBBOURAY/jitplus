package com.jitplus.merchant.data.model

data class LoyaltyProgram(
    val id: Long? = null,
    val merchantId: String,
    val name: String,
    val type: String, // "STAMPS" or "POINTS"
    val pointsPerVisit: Int,
    val threshold: Int,
    val rewardDescription: String
)
