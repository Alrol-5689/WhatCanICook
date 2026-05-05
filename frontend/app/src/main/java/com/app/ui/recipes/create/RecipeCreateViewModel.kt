package com.app.ui.recipes.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.IngredientDto
import com.app.dto.model.RecipeDetailDto
import com.app.dto.request.CreateRecipeRequest
import com.app.network.RetrofitClient
import com.app.repository.RecipeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeCreateViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository(RetrofitClient.recipeApi)

    private val _createdRecipe = MutableLiveData<RecipeDetailDto>()
    val createdRecipe: LiveData<RecipeDetailDto> = _createdRecipe

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _ingredients = MutableLiveData<List<IngredientDto>>()
    val ingredients: LiveData<List<IngredientDto>> = _ingredients

    private val _recipeToEdit = MutableLiveData<RecipeDetailDto>()
    val recipeToEdit: LiveData<RecipeDetailDto> = _recipeToEdit

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchIngredients() {
        _isLoading.value = true
        RetrofitClient.ingredientApi.getAllIngredients().enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(call: Call<List<IngredientDto>>, response: Response<List<IngredientDto>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _ingredients.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar ingredientes"
                }
            }

            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error conectando para ingredientes"
            }
        })
    }

    fun loadRecipeForEdit(recipeId: Long) {
        _isLoading.value = true
        recipeRepository.getRecipeById(recipeId).enqueue(object : Callback<RecipeDetailDto> {
            override fun onResponse(call: Call<RecipeDetailDto>, response: Response<RecipeDetailDto>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _recipeToEdit.value = response.body()
                } else {
                    _error.value = "Error al cargar receta para editar"
                }
            }

            override fun onFailure(call: Call<RecipeDetailDto>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error conectando para cargar receta"
            }
        })
    }

    fun createRecipe(
        title: String,
        description: String,
        publicRecipe: Boolean,
        userId: Long,
        ingredientIds: List<Long>,
        steps: List<String>
    ) {
        if (title.isBlank()) {
            _error.value = "El título es obligatorio"
            return
        }

        if (userId == -1L) {
            _error.value = "Usuario no válido"
            return
        }

        val cleanSteps = steps
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val request = CreateRecipeRequest(
            title = title.trim(),
            description = description.trim(),
            publicRecipe = publicRecipe,
            userId = userId,
            ingredientIds = ingredientIds,
            steps = cleanSteps
        )

        _isLoading.value = true
        recipeRepository.createRecipe(request).enqueue(object : Callback<RecipeDetailDto> {
            override fun onResponse(call: Call<RecipeDetailDto>, response: Response<RecipeDetailDto>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _createdRecipe.value = response.body()
                } else {
                    _error.value = "Error al crear la receta"
                }
            }

            override fun onFailure(call: Call<RecipeDetailDto>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    fun updateRecipe(
        recipeId: Long,
        title: String,
        description: String,
        publicRecipe: Boolean,
        userId: Long,
        ingredientIds: List<Long>,
        steps: List<String>
    ) {
        if (title.isBlank()) {
            _error.value = "El título es obligatorio"
            return
        }

        val cleanSteps = steps
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val request = CreateRecipeRequest(
            title = title.trim(),
            description = description.trim(),
            publicRecipe = publicRecipe,
            userId = userId,
            ingredientIds = ingredientIds,
            steps = cleanSteps
        )

        _isLoading.value = true
        recipeRepository.updateRecipe(recipeId, request).enqueue(object : Callback<RecipeDetailDto> {
            override fun onResponse(call: Call<RecipeDetailDto>, response: Response<RecipeDetailDto>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _createdRecipe.value = response.body() // Reusamos _createdRecipe para cerrar el fragmento
                } else {
                    _error.value = "Error al actualizar la receta"
                }
            }

            override fun onFailure(call: Call<RecipeDetailDto>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }
}

