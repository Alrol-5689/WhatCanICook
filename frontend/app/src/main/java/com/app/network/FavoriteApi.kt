package com.app.network

import com.app.dto.model.FavoriteRecipeDto
import com.app.dto.request.FavoriteRecipeRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteApi {

    @POST("favorites")
    fun addFavorite(@Body request: FavoriteRecipeRequest): Call<FavoriteRecipeDto>

    @GET("favorites/{userId}")
    fun getUserFavorites(@Path("userId") userId: Long): Call<List<FavoriteRecipeDto>>

    @DELETE("favorites")
    fun removeFavorite(@Query("userId") userId: Long, @Query("recipeId") recipeId: Long): Call<Void>
}
