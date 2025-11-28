package com.jitplus.merchant.data.model

data class Customer(
    val id: Long? = null,
    val phoneNumber: String,
    val name: String?,
    val email: String?,
    val consent: Boolean,
    val qrToken: String? = null
)

data class QrTokenResponse(
    val qrToken: String
)
