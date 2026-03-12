package com.app.model.response

data class AuthResponse (

    val success: Boolean,
    val message: String,
    val username: String?,
    val email: String?

    // token?

)