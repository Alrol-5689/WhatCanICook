package com.app.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)
