package com.app.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PantryViewModel : ViewModel() {

    // Lista observable de ingredientes
    private val _ingredients = MutableLiveData<List<String>>(mutableListOf())
    val ingredients: LiveData<List<String>> = _ingredients

    // Añade un ingrediente a la lista (solo si no está vacío y no es duplicado)
    fun addIngredient(name: String) {
        val currentList = _ingredients.value?.toMutableList() ?: mutableListOf()
        val normalizedName = name.trim().lowercase()

        if (normalizedName.isNotEmpty() && !currentList.contains(normalizedName)) {
            currentList.add(normalizedName)
            _ingredients.value = currentList
        }
    }

    // Elimina un ingrediente de la lista
    fun removeIngredient(name: String) {
        val currentList = _ingredients.value?.toMutableList() ?: mutableListOf()
        if (currentList.remove(name)) {
            _ingredients.value = currentList
        }
    }

    // Función para el botón "A COCINAR" (Aquí iría la llamada al servidor más adelante)
    fun findRecipes() {
        val ingredientList = _ingredients.value ?: emptyList()
        if (ingredientList.isNotEmpty()) {
            // TODO: Llamar al repositorio para buscar recetas con estos ingredientes
            println("Buscando recetas con: ${ingredientList.joinToString()}")
        }
    }
}