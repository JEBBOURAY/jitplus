package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.data.model.QrTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerService {
    @POST("customers")
    suspend fun registerCustomer(@Body customer: Customer): Response<Customer>

    @GET("customers/by-phone")
    suspend fun getCustomerByPhone(@Query("phone") phone: String): Response<Customer>
    
    @GET("customers/{id}/qr-token")
    suspend fun getQrToken(@Path("id") customerId: Long): Response<QrTokenResponse>
    
    @GET("customers/by-qr-token")
    suspend fun getCustomerByQrToken(@Query("token") token: String): Response<Customer>
}
