package com.app.dto.response

import com.app.dto.model.UserDto

data class AuthResponse (

    val success: Boolean,
    val message: String,
    val user: UserDto?

    // token?

)