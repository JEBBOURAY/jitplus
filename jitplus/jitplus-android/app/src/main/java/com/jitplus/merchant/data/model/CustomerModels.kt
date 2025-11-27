package com.jitplus.merchant.data.model

data class Customer(
    val id: Long? = null,
    val phoneNumber: String,
    val name: String?,
    val email: String?,
    val consent: Boolean
)

data class LoyaltyCard(
    val id: Long,
    val merchantId: String,
    val customerId: Long,
    val currentPoints: Int,
    val currentStamps: Int
)
