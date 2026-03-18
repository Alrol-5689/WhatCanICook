package com.app.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.RecipeDetailDto
import com.app.network.RetrofitClient
import com.app.repository.RecipeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailViewModel : ViewModel() {

    private val repository = RecipeRepository(RetrofitClient.recipeApi)

    private val _recipe = MutableLiveData<RecipeDetailDto>()
    val recipe: LiveData<RecipeDetailDto> = _recipe

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadRecipeDetail(recipeId: Long) {
        repository.getRecipeById(recipeId).enqueue(object : Callback<RecipeDetailDto> {
            override fun onResponse(
                call: Call<RecipeDetailDto>,
                response: Response<RecipeDetailDto>
            ) {
                if (response.isSuccessful) {
                    _recipe.value = response.body()
                } else {
                    _error.value = "Error al cargar la receta"
                }
            }

            override fun onFailure(call: Call<RecipeDetailDto>, t: Throwable) {
                _error.value = t.message ?: "Error desconocido"
            }
        })
    }
}