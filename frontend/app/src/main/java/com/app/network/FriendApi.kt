package com.app.network

import com.app.dto.model.FriendDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {

    @POST("friends/request")
    fun sendFriendRequest(@Query("requesterId") requesterId: Long,
                          @Query("receiverId") receiverId: Long): Call<FriendDto>

    @PUT("friends/{id}/accept")
    fun acceptFriend(@Path("id") id: Long): Call<FriendDto>

    @PUT("friends/{id}/reject")
    fun rejectFriend(@Path("id") id: Long): Call<FriendDto>

    @GET("friends/user/{userId}")
    fun getUserFriends(@Path("userId") userId: Long): Call<List<FriendDto>>
}