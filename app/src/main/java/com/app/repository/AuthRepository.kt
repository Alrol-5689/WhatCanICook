package com.app.repository

import com.app.model.request.LoginRequest
import com.app.model.request.RegisterRequest
import com.app.model.response.AuthResponse
import com.app.network.AuthApi
import retrofit2.Call

class AuthRepository(private val authApi: AuthApi) {

    fun login(request: LoginRequest): Call<AuthResponse> {
        return authApi.login(request)
    }

    fun register(request: RegisterRequest): Call<AuthResponse> {
        return authApi.register(request)
    }
}