package com.app.network

import com.app.dto.request.LoginRequest
import com.app.dto.request.RegisterRequest
import com.app.dto.response.AuthResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>
}







