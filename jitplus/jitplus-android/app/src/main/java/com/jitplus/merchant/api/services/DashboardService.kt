package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.DashboardStats
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardService {
    @GET("loyalty/dashboard/{merchantId}/stats")
    suspend fun getStats(@Path("merchantId") merchantId: String): Response<DashboardStats>
}
