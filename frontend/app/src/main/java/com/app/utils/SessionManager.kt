package com.app.utils

import com.app.BuildConfig

object SessionManager {
    var userId: Long = -1L
    var username: String? = null
    var email: String? = null
    var profileImageUrl: String? = null

    fun login(id: Long, name: String, userEmail: String, imageUrl: String? = null) {
        userId = id
        username = name
        email = userEmail
        profileImageUrl = imageUrl
    }

    fun logout() {
        userId = -1L
        username = null
        email = null
        profileImageUrl = null
    }

    fun isLoggedIn(): Boolean = userId != -1L

    fun getAbsoluteProfileImageUrl(): String? {
        val url = profileImageUrl?.trim().orEmpty()
        if (url.isBlank()) return null
        if (url.startsWith("http://") || url.startsWith("https://")) return url

        return BuildConfig.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
    }
}
