package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.data.model.VisitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoyaltyService {
    @POST("loyalty/programs")
    suspend fun createProgram(@Body program: LoyaltyProgram): Response<LoyaltyProgram>

    @GET("loyalty/programs/{merchantId}")
    suspend fun getProgram(@Path("merchantId") merchantId: String): Response<LoyaltyProgram>

    @POST("loyalty/cards")
    suspend fun createCard(
        @Query("merchantId") merchantId: String, 
        @Query("customerId") customerId: Long,
        @Query("customerName") customerName: String? = null,
        @Query("customerPhone") customerPhone: String? = null
    ): Response<LoyaltyCard>

    @GET("loyalty/cards")
    suspend fun getCard(@Query("merchantId") merchantId: String, @Query("customerId") customerId: Long): Response<LoyaltyCard>

    @POST("loyalty/visits")
    suspend fun recordVisit(@Body request: VisitRequest): Response<VisitResponse>

    @POST("loyalty/redemptions")
    suspend fun redeemReward(@Body request: RedemptionRequest): Response<LoyaltyCard>
}
