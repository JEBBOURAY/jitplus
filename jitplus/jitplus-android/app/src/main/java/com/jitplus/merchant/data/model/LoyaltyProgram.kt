package com.jitplus.merchant.data.model

data class LoyaltyProgram(
    val id: Long? = null,
    val merchantId: String,
    val name: String,
    val type: String, // "STAMPS", "POINTS", "PROGRESSIVE"
    val pointsPerVisit: Int,
    val threshold: Int,
    val rewardDescription: String,
    // Happy Hour
    val happyHourEnabled: Boolean = false,
    val happyHourStart: String? = null,
    val happyHourEnd: String? = null,
    val happyHourMultiplier: Double = 1.0,
    // Progressive
    val progressiveStep: Int = 5,
    val progressiveBonus: Int = 1
)
