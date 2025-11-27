package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.data.model.VisitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoyaltyService {
    @POST("loyalty/programs")
    fun createProgram(@Body program: LoyaltyProgram): Call<LoyaltyProgram>

    @GET("loyalty/programs/{merchantId}")
    fun getProgram(@Path("merchantId") merchantId: String): Call<LoyaltyProgram>

    @POST("loyalty/cards")
    fun createCard(@Query("merchantId") merchantId: String, @Query("customerId") customerId: Long): Call<LoyaltyCard>

    @GET("loyalty/cards")
    fun getCard(@Query("merchantId") merchantId: String, @Query("customerId") customerId: Long): Call<LoyaltyCard>

    @POST("loyalty/visits")
    fun recordVisit(@Body request: VisitRequest): Call<VisitResponse>

    @POST("loyalty/redemptions")
    fun redeemReward(@Body request: RedemptionRequest): Call<LoyaltyCard>
}
