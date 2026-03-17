package com.app.dto.model

data class FriendDto(

    val id: Long,

    val requesterId: Long,
    val requesterUsername: String,

    val receiverId: Long,
    val receiverUsername: String,

    val status: FriendStatus,

    val createdAt: String


)
