package com.app.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.IngredientDto
import com.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryViewModel : ViewModel() {

    private val _availableIngredients = MutableLiveData<List<IngredientDto>>()
    val availableIngredients: LiveData<List<IngredientDto>> = _availableIngredients

    private val _selectedIngredients = MutableLiveData<List<IngredientDto>>(emptyList())
    val selectedIngredients: LiveData<List<IngredientDto>> = _selectedIngredients

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchIngredients() {
        RetrofitClient.ingredientApi.getAllIngredients().enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(
                call: Call<List<IngredientDto>>,
                response: Response<List<IngredientDto>>
            ) {
                if (response.isSuccessful) {
                    _availableIngredients.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar ingredientes"
                }
            }

            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    fun addIngredient(ingredient: IngredientDto) {
        val current = _selectedIngredients.value ?: emptyList()
        if (current.any { it.id == ingredient.id }) return
        _selectedIngredients.value = current + ingredient
    }

    fun removeIngredient(ingredientId: Long) {
        val current = _selectedIngredients.value ?: emptyList()
        _selectedIngredients.value = current.filterNot { it.id == ingredientId }
    }

    fun getSelectedIngredientIds(): ArrayList<Long> {
        return ArrayList((_selectedIngredients.value ?: emptyList()).map { it.id })
    }
}
