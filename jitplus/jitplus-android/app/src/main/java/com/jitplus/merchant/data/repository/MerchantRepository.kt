package com.jitplus.merchant.data.repository

import android.content.Context
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.AuthService
import com.jitplus.merchant.api.services.CustomerService
import com.jitplus.merchant.api.services.DashboardService
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.data.model.DashboardStats
import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.QrTokenResponse
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.RegisterRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.data.model.VisitResponse
import retrofit2.Response

class MerchantRepository(context: Context) {

    private val authService: AuthService = ApiClient.getClient(context).create(AuthService::class.java)
    private val dashboardService: DashboardService = ApiClient.getClient(context).create(DashboardService::class.java)
    private val customerService: CustomerService = ApiClient.getClient(context).create(CustomerService::class.java)
    private val loyaltyService: LoyaltyService = ApiClient.getClient(context).create(LoyaltyService::class.java)

    // Auth
    suspend fun login(request: LoginRequest): Response<String> {
        return authService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<String> {
        return authService.register(request)
    }

    // Dashboard
    suspend fun getDashboardStats(merchantId: String): Response<DashboardStats> {
        return dashboardService.getStats(merchantId)
    }

    // Customer
    suspend fun getCustomerByPhone(phone: String): Response<Customer> {
        return customerService.getCustomerByPhone(phone)
    }

    suspend fun getCustomerByQrToken(token: String): Response<Customer> {
        return customerService.getCustomerByQrToken(token)
    }

    suspend fun getQrToken(customerId: Long): Response<QrTokenResponse> {
        return customerService.getQrToken(customerId)
    }

    suspend fun registerCustomer(customer: Customer): Response<Customer> {
        return customerService.registerCustomer(customer)
    }

    // Loyalty
    suspend fun createProgram(program: LoyaltyProgram): Response<LoyaltyProgram> {
        return loyaltyService.createProgram(program)
    }

    suspend fun getProgram(merchantId: String): Response<LoyaltyProgram> {
        return loyaltyService.getProgram(merchantId)
    }

    suspend fun createCard(merchantId: String, customerId: Long, name: String?, phone: String?): Response<LoyaltyCard> {
        return loyaltyService.createCard(merchantId, customerId, name, phone)
    }

    suspend fun getCard(merchantId: String, customerId: Long): Response<LoyaltyCard> {
        return loyaltyService.getCard(merchantId, customerId)
    }

    suspend fun recordVisit(request: VisitRequest): Response<VisitResponse> {
        return loyaltyService.recordVisit(request)
    }

    suspend fun redeemReward(request: RedemptionRequest): Response<LoyaltyCard> {
        return loyaltyService.redeemReward(request)
    }
}
