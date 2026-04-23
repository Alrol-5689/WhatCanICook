package com.app.utils

object SessionManager {
    var userId: Long = -1L
    var username: String? = null
    var email: String? = null

    fun login(id: Long, name: String, userEmail: String) {
        userId = id
        username = name
        email = userEmail
    }

    fun logout() {
        userId = -1L
        username = null
        email = null
    }

    fun isLoggedIn(): Boolean = userId != -1L
}

