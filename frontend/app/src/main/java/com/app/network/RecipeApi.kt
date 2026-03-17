package com.app.network

import com.app.dto.model.RecipeSummaryDto
import com.app.dto.model.RecipeDetailDto
import com.app.dto.request.CreateRecipeRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    @GET("recipes/public")
    fun getPublicRecipes(): Call<List<RecipeSummaryDto>>

    @GET("recipes/{id}")
    fun getRecipeById(@Path("id") id: Long): Call<RecipeDetailDto>

    @GET("recipes/search")
    fun searchRecipes(@Query("title") title: String): Call<List<RecipeSummaryDto>>

    @POST("recipes")
    fun createRecipe(@Body request: CreateRecipeRequest): Call<RecipeDetailDto>

}