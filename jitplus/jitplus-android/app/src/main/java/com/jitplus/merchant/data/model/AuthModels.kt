package com.jitplus.merchant.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val phone: String? = null,
    val shopName: String? = null,
    val city: String? = null,
    val address: String? = null
)

data class StoreInfoRequest(
    val email: String,
    val shopName: String,
    val shopType: String,
    val city: String,
    val address: String?,
    val website: String? = null,
    val instagram: String? = null,
    val language: String? = "FR",
    val timezone: String? = null
)

// The token is returned as a plain string in the current backend implementation
// But usually it's better to wrap it in a JSON object. 
// For now, since the backend returns a String, we might need to handle it carefully with Retrofit.
// Retrofit expects JSON by default. 
// Let's update the backend to return a JSON object or handle String response in Android.
// I'll assume for now we can handle the string or I'll update the backend to return a JSON object.
// Let's update the backend AuthController to return a JSON object for the token.
