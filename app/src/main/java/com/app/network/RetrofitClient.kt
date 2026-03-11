package com.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.130:8080/"

    /* -> BASE_URL =
    con mv: "http://LA IP DE TU ORDENADOR:8080/"   || my y PC en la misma wifi ||
    MV -> "http://10.0.2.2:8080/"*/

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}