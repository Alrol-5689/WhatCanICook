package com.app.model.response

import com.app.model.entity.User

data class AuthResponse (

    val success: Boolean,
    val message: String,
    val user: User?

    // token?

)