package com.app.dto.model

data class RecipeDetailDto(

    val id: Long,
    val title: String,
    val description: String,
    val publicRecipe: Boolean,
    val userId: Long,
    val username: String,
    val createdAt: String,
    val ingredients: List<IngredientDto> = emptyList(),
    val steps: List<RecipeStepDto> = emptyList()

)
