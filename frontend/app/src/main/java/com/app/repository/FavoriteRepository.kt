package com.app.repository

import com.app.dto.model.FavoriteRecipeDto
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.FavoriteApi
import retrofit2.Call

class FavoriteRepository(private val favoriteApi: FavoriteApi) {

    fun addFavorite(request: FavoriteRecipeRequest): Call<FavoriteRecipeDto> {
        return favoriteApi.addFavorite(request)
    }

    fun getUserFavorites(userId: Long): Call<List<FavoriteRecipeDto>> {
        return favoriteApi.getUserFavorites(userId)
    }

    fun removeFavorite(userId: Long, recipeId: Long): Call<Void> {
        return favoriteApi.removeFavorite(userId, recipeId)
    }
}