package com.app.repository

import com.app.dto.model.RecipeDetailDto
import com.app.dto.model.RecipeSummaryDto
import com.app.dto.request.CreateRecipeRequest
import com.app.dto.request.RecipesByIngredientIdsRequest
import com.app.dto.request.RecipesByIngredientsRequest
import com.app.network.RecipeApi
import retrofit2.Call

// Este repository actúa como intermediario entre el ViewModel y Retrofit.
// Importante: los métodos devuelven Call<T>, no los datos directamente.
// Eso significa que aquí NO obtenemos resultados, solo preparamos la llamada HTTP.
class RecipeRepository(private val recipeApi: RecipeApi) {

    fun getPublicRecipes(): Call<List<RecipeSummaryDto>> {
        return recipeApi.getPublicRecipes()
    }

    fun getRecipesByUser(userId: Long): Call<List<RecipeSummaryDto>> {
        return recipeApi.getRecipesByUser(userId)
    }

    // Esta función NO hace la llamada HTTP todavía.
    // Solo devuelve un objeto Call, que es como "una llamada preparada" (un botón).
    // La llamada real se ejecuta cuando en el ViewModel se usa .enqueue(...)
    fun getRecipeById(id: Long): Call<RecipeDetailDto> {
        // Aquí simplemente delegamos en Retrofit (RecipeApi)
        // Retrofit construye la petición HTTP pero NO la ejecuta aún
        return recipeApi.getRecipeById(id) // devuelve un objeto Call (petición preparada)
    }

    fun searchRecipes(title: String): Call<List<RecipeSummaryDto>> {
        return recipeApi.searchRecipes(title)
    }

    fun createRecipe(request: CreateRecipeRequest): Call<RecipeDetailDto> {
        return recipeApi.createRecipe(request)
    }

    fun updateRecipe(id: Long, request: CreateRecipeRequest): Call<RecipeDetailDto> {
        return recipeApi.updateRecipe(id, request)
    }

    fun deleteRecipe(id: Long, userId: Long): Call<Void> {
        return recipeApi.deleteRecipe(id, userId)
    }

    fun getPublicRecipesByIngredients(request: RecipesByIngredientsRequest): Call<List<RecipeSummaryDto>> {
        return recipeApi.getPublicRecipesByIngredients(request)
    }

    fun getPublicRecipesByIngredientIds(request: RecipesByIngredientIdsRequest): Call<List<RecipeSummaryDto>> {
        return recipeApi.getPublicRecipesByIngredientIds(request)
    }
}
