package com.jitplus.merchant.api

import android.content.Context
import com.jitplus.merchant.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {
    // For Android Emulator, localhost is 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager(context)
            val authInterceptor = AuthInterceptor(tokenManager)

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
