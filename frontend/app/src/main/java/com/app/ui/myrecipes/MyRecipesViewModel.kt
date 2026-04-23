package com.app.ui.myrecipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.RecipeSummaryDto
import com.app.network.RetrofitClient
import com.app.repository.RecipeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyRecipesViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository(RetrofitClient.recipeApi)

    private val _recipes = MutableLiveData<List<RecipeSummaryDto>>()
    val recipes: LiveData<List<RecipeSummaryDto>> = _recipes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadMyRecipes(userId: Long) {
        recipeRepository.getRecipesByUser(userId).enqueue(
            object : Callback<List<RecipeSummaryDto>> {
                override fun onResponse(
                    call: Call<List<RecipeSummaryDto>>,
                    response: Response<List<RecipeSummaryDto>>
                ) {
                    if (response.isSuccessful) {
                        _recipes.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al cargar mis recetas"
                    }
                }

                override fun onFailure(call: Call<List<RecipeSummaryDto>>, t: Throwable) {
                    _error.value = t.message ?: "Error desconocido"
                }
            }
        )
    }
}
