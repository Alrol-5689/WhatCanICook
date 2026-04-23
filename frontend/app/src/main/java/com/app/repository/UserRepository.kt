package com.app.repository

import com.app.dto.model.UserDto
import com.app.network.UserApi
import retrofit2.Call

class UserRepository(private val userApi: UserApi) {
    fun searchUsers(username: String?): Call<List<UserDto>> = userApi.searchUsers(username)
}

