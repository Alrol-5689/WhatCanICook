package com.app.network

import com.app.dto.model.UserDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    fun searchUsers(@Query("username") username: String?): Call<List<UserDto>>

    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: Long): Call<UserDto>

    @Multipart
    @POST("users/{userId}/profile-image")
    fun uploadProfileImage(
        @Path("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): Call<UserDto>
}
