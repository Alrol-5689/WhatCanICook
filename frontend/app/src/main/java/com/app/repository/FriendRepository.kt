package com.app.repository

import com.app.dto.model.FriendDto
import com.app.dto.request.FriendRequest
import com.app.network.FriendApi
import retrofit2.Call

class FriendRepository(private val friendApi: FriendApi) {

    fun acceptFriend(id: Long): Call<FriendDto> {
        return friendApi.acceptFriend(id)
    }

    fun rejectFriend(id: Long): Call<FriendDto> {
        return friendApi.rejectFriend(id)
    }

    fun sendFriendRequest(request: FriendRequest): Call<FriendDto> = friendApi.sendFriendRequest(request)

    fun getPendingRequests(userId: Long): Call<List<FriendDto>> = friendApi.getPendingRequests(userId)
}
