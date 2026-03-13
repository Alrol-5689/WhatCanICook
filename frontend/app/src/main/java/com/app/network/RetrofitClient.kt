package com.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.app.BuildConfig

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val instance: Retrofit
        get() = retrofit
}