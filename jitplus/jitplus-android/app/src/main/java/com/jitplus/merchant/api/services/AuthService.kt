package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.data.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/token")
    fun login(@Body request: LoginRequest): Call<String> // Backend returns String

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<String>
}
