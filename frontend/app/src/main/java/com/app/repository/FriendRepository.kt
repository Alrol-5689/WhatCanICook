package com.app.repository

import com.app.dto.model.FriendDto
import com.app.network.FriendApi
import retrofit2.Call

class FriendRepository(private val friendApi: FriendApi) {

    fun sendFriendRequest(requesterId: Long, receiverId: Long): Call<FriendDto> {
        return friendApi.sendFriendRequest(requesterId, receiverId)
    }

    fun acceptFriend(id: Long): Call<FriendDto> {
        return friendApi.acceptFriend(id)
    }

    fun rejectFriend(id: Long): Call<FriendDto> {
        return friendApi.rejectFriend(id)
    }

    fun getUserFriends(userId: Long): Call<List<FriendDto>> {
        return friendApi.getUserFriends(userId)
    }
}