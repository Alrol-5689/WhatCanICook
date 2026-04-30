package com.app.repository

import com.app.dto.model.IngredientDto
import com.app.dto.model.UserDto
import com.app.network.UserApi
import retrofit2.Call

class UserRepository(private val userApi: UserApi) {
    fun searchUsers(username: String?): Call<List<UserDto>> = userApi.searchUsers(username)

    fun getPantry(userId: Long): Call<List<IngredientDto>> = userApi.getPantry(userId)
    fun addToPantry(userId: Long, ingredientId: Long): Call<List<IngredientDto>> = userApi.addToPantry(userId, ingredientId)
    fun removeFromPantry(userId: Long, ingredientId: Long): Call<List<IngredientDto>> = userApi.removeFromPantry(userId, ingredientId)
}

