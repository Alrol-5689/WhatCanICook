package com.app.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.RecipeSummaryDto
import com.app.network.RetrofitClient
import com.app.repository.FavoriteRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository(RetrofitClient.favoriteApi)

    private val _recipes = MutableLiveData<List<RecipeSummaryDto>>()
    val recipes: LiveData<List<RecipeSummaryDto>> = _recipes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadFavoriteRecipes(userId: Long) {
        favoriteRepository.getUserFavorites(userId).enqueue(
            object : Callback<List<com.app.dto.model.FavoriteRecipeDto>> {
                override fun onResponse(
                    call: Call<List<com.app.dto.model.FavoriteRecipeDto>>,
                    response: Response<List<com.app.dto.model.FavoriteRecipeDto>>
                ) {
                    if (response.isSuccessful) {
                        val favorites = response.body() ?: emptyList()
                        _recipes.value = favorites.map { it.recipe }
                    } else {
                        _error.value = "Error al cargar favoritas"
                    }
                }

                override fun onFailure(
                    call: Call<List<com.app.dto.model.FavoriteRecipeDto>>,
                    t: Throwable
                ) {
                    _error.value = t.message ?: "Error desconocido"
                }
            }
        )
    }
}
