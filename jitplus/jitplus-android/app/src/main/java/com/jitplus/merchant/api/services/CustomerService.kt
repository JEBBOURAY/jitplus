package com.jitplus.merchant.api.services

import com.jitplus.merchant.data.model.Customer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomerService {
    @POST("customers")
    fun registerCustomer(@Body customer: Customer): Call<Customer>

    @GET("customers/by-phone")
    fun getCustomerByPhone(@Query("phone") phone: String): Call<Customer>
}
