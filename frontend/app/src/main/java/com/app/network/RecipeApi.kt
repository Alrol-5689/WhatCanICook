package com.app.network

import com.app.dto.model.RecipeSummaryDto
import com.app.dto.model.RecipeDetailDto
import com.app.dto.request.CreateRecipeRequest
import com.app.dto.request.RecipesByIngredientIdsRequest
import com.app.dto.request.RecipesByIngredientsRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    @GET("recipes/public")
    fun getPublicRecipes(): Call<List<RecipeSummaryDto>>

    @GET("recipes/user/{userId}")
    fun getRecipesByUser(@Path("userId") userId: Long): Call<List<RecipeSummaryDto>>

    @GET("recipes/{id}")
    fun getRecipeById(@Path("id") id: Long): Call<RecipeDetailDto>

    @GET("recipes/search")
    fun searchRecipes(@Query("title") title: String): Call<List<RecipeSummaryDto>>

    @POST("recipes")
    fun createRecipe(@Body request: CreateRecipeRequest): Call<RecipeDetailDto>

    @PUT("recipes/{id}")
    fun updateRecipe(@Path("id") id: Long, @Body request: CreateRecipeRequest): Call<RecipeDetailDto>

    @DELETE("recipes/{id}")
    fun deleteRecipe(@Path("id") id: Long, @Query("userId") userId: Long): Call<Void>

    @POST("recipes/by-ingredients")
    fun getPublicRecipesByIngredients(@Body request: RecipesByIngredientsRequest): Call<List<RecipeSummaryDto>>

    @POST("recipes/by-ingredient-ids")
    fun getPublicRecipesByIngredientIds(@Body request: RecipesByIngredientIdsRequest): Call<List<RecipeSummaryDto>>

}
