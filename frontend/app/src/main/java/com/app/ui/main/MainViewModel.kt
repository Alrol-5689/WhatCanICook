package com.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.RecipeSummaryDto
import com.app.network.RetrofitClient
import com.app.repository.RecipeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository(RetrofitClient.recipeApi)

    private val _recipes = MutableLiveData<List<RecipeSummaryDto>>()
    val recipes: LiveData<List<RecipeSummaryDto>> = _recipes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadPublicRecipes() {
        val mockData = listOf(
            RecipeSummaryDto(1L, "Macarrones gratinados", "Deliciosa pasta de prueba", true, 2L, "ChefGonzalo", "2026-04-08"),
            RecipeSummaryDto(2L, "Ensalada César", "Mock recipe list", true, 3L, "Tester1", "2026-04-07"),
            RecipeSummaryDto(3L, "Salmón al Horno", "Pescado", true, 4L, "Tester2", "2026-04-06")
        )
        _recipes.value = mockData
    }
}