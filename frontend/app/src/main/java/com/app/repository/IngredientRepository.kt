package com.app.repository

import com.app.dto.model.IngredientDto
import com.app.network.IngredientApi
import retrofit2.Call

class IngredientRepository(private val ingredientApi: IngredientApi) {

    fun getAllIngredients(): Call<List<IngredientDto>> {
        return ingredientApi.getAllIngredients()
    }

    fun searchIngredients(name: String): Call<List<IngredientDto>> {
        return ingredientApi.searchIngredients(name)
    }
}