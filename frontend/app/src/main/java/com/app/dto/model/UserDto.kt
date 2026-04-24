package com.app.dto.model

data class UserDto(

    val id: Long,
    val username: String,
    val email: String,
    val profileImageUrl: String? = null

)
