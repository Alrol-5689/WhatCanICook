package com.app.network

import com.app.dto.model.UserDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    fun searchUsers(@Query("username") username: String?): Call<List<UserDto>>

    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: Long): Call<UserDto>
}

