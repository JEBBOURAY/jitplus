package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/token")
    suspend fun login(@Body request: LoginRequest): Response<String> // Backend returns String

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @retrofit2.http.PUT("auth/update")
    suspend fun updateStoreInfo(@Body request: com.jitplus.merchant.data.model.StoreInfoRequest): Response<String>
}
