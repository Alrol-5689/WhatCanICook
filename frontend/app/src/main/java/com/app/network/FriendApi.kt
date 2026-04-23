package com.app.network

import com.app.dto.model.FriendDto
import com.app.dto.request.FriendRequest
import com.app.dto.response.ApiMessageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {

    @POST("friends/request")
    fun sendFriendRequest(@Body request: FriendRequest): Call<FriendDto>

    @GET("friends/pending/{userId}")
    fun getPendingRequests(@Path("userId") userId: Long): Call<List<FriendDto>>

    @GET("friends/accepted/{userId}")
    fun getAcceptedFriends(@Path("userId") userId: Long): Call<List<FriendDto>>

    @PATCH("friends/{id}/accept")
    fun acceptFriend(@Path("id") id: Long): Call<FriendDto>

    @PATCH("friends/{id}/reject")
    fun rejectFriend(@Path("id") id: Long): Call<FriendDto>

    @DELETE("friends")
    fun removeFriendship(
        @Query("userId") userId: Long,
        @Query("friendUserId") friendUserId: Long
    ): Call<ApiMessageResponse>
}
