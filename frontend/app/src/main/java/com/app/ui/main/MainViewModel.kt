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
        // getPublicRecipes solo es el botón de la llamada
        recipeRepository.getPublicRecipes().enqueue( // enqueue ejecuta la llamada

            object : Callback<List<RecipeSummaryDto>> {

                override fun onResponse(
                    call: Call<List<RecipeSummaryDto>>,
                    response: Response<List<RecipeSummaryDto>>
                ) {
                    if (response.isSuccessful) {
                        // La llamada HTTP ha respondido correctamente (200 OK)
                        // Ahora extraemos el body (los datos reales)
                        _recipes.value = response.body()
                    } else {
                        _error.value = "Error al cargar recetas"
                    }
                }

                override fun onFailure(
                    call: Call<List<RecipeSummaryDto>>,
                    t: Throwable
                ) {
                    _error.value = t.message ?: "Error desconocido"
                }

            }
        )
    }
}