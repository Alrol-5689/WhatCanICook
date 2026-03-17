package com.app.repository

import com.app.dto.model.RecipeDetailDto
import com.app.dto.model.RecipeSummaryDto
import com.app.dto.request.CreateRecipeRequest
import com.app.network.RecipeApi
import retrofit2.Call

class RecipeRepository(private val recipeApi: RecipeApi) {

    fun getPublicRecipes(): Call<List<RecipeSummaryDto>> {
        return recipeApi.getPublicRecipes()
    }

    fun getRecipeById(id: Long): Call<RecipeDetailDto> {
        return recipeApi.getRecipeById(id)
    }

    fun searchRecipes(title: String): Call<List<RecipeSummaryDto>> {
        return recipeApi.searchRecipes(title)
    }

    fun createRecipe(request: CreateRecipeRequest): Call<RecipeDetailDto> {
        return recipeApi.createRecipe(request)
    }
}