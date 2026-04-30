package com.app.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dto.model.IngredientDto
import com.app.network.RetrofitClient
import com.app.utils.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<IngredientDto>>()
    val searchResults: LiveData<List<IngredientDto>> = _searchResults

    private val _availableIngredients = MutableLiveData<List<IngredientDto>>()
    val availableIngredients: LiveData<List<IngredientDto>> = _availableIngredients

    private val _selectedIngredients = MutableLiveData<List<IngredientDto>>(emptyList())
    val selectedIngredients: LiveData<List<IngredientDto>> = _selectedIngredients

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Variables para el control de la búsqueda
    private var searchJob: Job? = null
    private var lastQuery: String? = null

    init {
        loadPantry()
        loadAvailableIngredients()
    }

    private fun loadAvailableIngredients() {
        RetrofitClient.ingredientApi.getAllIngredients().enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(call: Call<List<IngredientDto>>, response: Response<List<IngredientDto>>) {
                if (response.isSuccessful) {
                    _availableIngredients.value = response.body() ?: emptyList()
                }
            }
            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                // Ignore error, maybe just log
            }
        })
    }

    private fun loadPantry() {
        val userId = SessionManager.userId
        if (userId == -1L) return

        RetrofitClient.userApi.getPantry(userId).enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(
                call: Call<List<IngredientDto>>,
                response: Response<List<IngredientDto>>
            ) {
                if (response.isSuccessful) {
                    _selectedIngredients.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar la despensa"
                }
            }

            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                _error.value = "Error de conexión al cargar la despensa"
            }
        })
    }

    /**
     * Busca ingredientes con Debounce y limpieza de texto.
     */
    fun searchIngredients(query: String) {
        val trimmedQuery = query.trim()

        // 1. Evitamos buscar lo mismo dos veces seguidas
        if (trimmedQuery == lastQuery) return
        lastQuery = trimmedQuery

        // 2. Cancelamos cualquier búsqueda que estuviera en curso
        searchJob?.cancel()

        // 3. Si el texto es muy corto, limpiamos resultados y no buscamos
        if (trimmedQuery.length < 2) {
            _searchResults.value = emptyList()
            return
        }

        // 4. Lanzamos la búsqueda con un retraso (Debounce de 300ms)
        searchJob = viewModelScope.launch {
            delay(300)
            executeSearch(trimmedQuery)
        }
    }

    private fun executeSearch(query: String) {
        RetrofitClient.ingredientApi.searchIngredients(query).enqueue(object : Callback<List<IngredientDto>> {
            override fun onResponse(
                call: Call<List<IngredientDto>>,
                response: Response<List<IngredientDto>>
            ) {
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al buscar ingredientes"
                }
            }

            override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                // Si la llamada fue cancelada por nosotros, no lo tratamos como error
                if (!call.isCanceled) {
                    _error.value = "Error de conexión"
                }
            }
        })
    }

    fun addIngredient(ingredient: IngredientDto) {
        val current = _selectedIngredients.value ?: emptyList()
        if (current.any { it.id == ingredient.id }) return
        
        // Optimistic UI update
        _selectedIngredients.value = current + ingredient
        
        // Al añadir, limpiamos la búsqueda para que el usuario pueda escribir el siguiente
        _searchResults.value = emptyList()
        lastQuery = ""

        val userId = SessionManager.userId
        if (userId != -1L) {
            RetrofitClient.userApi.addToPantry(userId, ingredient.id).enqueue(object : Callback<List<IngredientDto>> {
                override fun onResponse(call: Call<List<IngredientDto>>, response: Response<List<IngredientDto>>) {
                    if (response.isSuccessful) {
                        _selectedIngredients.value = response.body() ?: emptyList()
                    }
                }
                override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                    _error.value = "Error al guardar en la despensa"
                }
            })
        }
    }

    fun removeIngredient(ingredientId: Long) {
        val current = _selectedIngredients.value ?: emptyList()
        // Optimistic UI update
        _selectedIngredients.value = current.filterNot { it.id == ingredientId }

        val userId = SessionManager.userId
        if (userId != -1L) {
            RetrofitClient.userApi.removeFromPantry(userId, ingredientId).enqueue(object : Callback<List<IngredientDto>> {
                override fun onResponse(call: Call<List<IngredientDto>>, response: Response<List<IngredientDto>>) {
                    if (response.isSuccessful) {
                        _selectedIngredients.value = response.body() ?: emptyList()
                    }
                }
                override fun onFailure(call: Call<List<IngredientDto>>, t: Throwable) {
                    _error.value = "Error al quitar de la despensa"
                }
            })
        }
    }

    fun getSelectedIngredientIds(): ArrayList<Long> {
        return ArrayList((_selectedIngredients.value ?: emptyList()).map { it.id })
    }

    fun createNewIngredient(name: String) {
        val newIngredient = IngredientDto(
            id = 0, // Backend asignará el ID
            name = name,
            castellano = name,
            carbs100g = 0.0,
            protein100g = 0.0,
            fat100g = 0.0,
            fiber100g = 0.0
        )
        RetrofitClient.ingredientApi.createIngredient(newIngredient).enqueue(object : Callback<IngredientDto> {
            override fun onResponse(call: Call<IngredientDto>, response: Response<IngredientDto>) {
                if (response.isSuccessful) {
                    val created = response.body()
                    if (created != null) {
                        // Lo añadimos a disponibles y lo seleccionamos automáticamente
                        _availableIngredients.value = (_availableIngredients.value ?: emptyList()) + created
                        addIngredient(created)
                    }
                } else {
                    _error.value = "Error al crear ingrediente"
                }
            }
            override fun onFailure(call: Call<IngredientDto>, t: Throwable) {
                _error.value = "Error de conexión al crear ingrediente"
            }
        })
    }
}
