package com.app.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.IngredientDto
import com.app.dto.model.RecipeSummaryDto
import com.app.dto.request.RecipesByIngredientIdsRequest
import com.app.network.RetrofitClient
import com.app.repository.RecipeRepository
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryResultsViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository(RetrofitClient.recipeApi)

    private val _recipes = MutableLiveData<List<RecipeSummaryDto>>()
    val recipes: LiveData<List<RecipeSummaryDto>> = _recipes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadRecipesFromPantry() {
        val userId = SessionManager.userId
        if (userId == -1L) {
            _recipes.value = emptyList()
            return
        }

        RetrofitClient.userApi.getPantry(userId).enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(
                call: Call<List<IngredientDto>>,
                response: Response<List<IngredientDto>>
            ) {
                if (response.isSuccessful) {
                    loadRecipes((response.body() ?: emptyList()).map { it.id })
                } else {
                    _error.value = "Error al cargar la despensa"
                }
            }

            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    private fun loadRecipes(ingredientIds: List<Long>) {
        val normalized = ingredientIds.filter { it > 0 }.distinct()
        if (normalized.isEmpty()) {
            _recipes.value = emptyList()
            return
        }

        recipeRepository.getPublicRecipesByIngredientIds(RecipesByIngredientIdsRequest(normalized))
            .enqueue(object : Callback<List<RecipeSummaryDto>> {
                override fun onResponse(
                    call: Call<List<RecipeSummaryDto>>,
                    response: Response<List<RecipeSummaryDto>>
                ) {
                    if (response.isSuccessful) {
                        _recipes.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al buscar recetas"
                    }
                }

                override fun onFailure(call: Call<List<RecipeSummaryDto>>, t: Throwable) {
                    _error.value = t.message ?: "Error de conexión"
                }
            })
    }
}
