package com.app.dto.request

data class CreateRecipeRequest(

    val title: String,

    val description: String,

    val publicRecipe: Boolean,

    val userId: Long,

    val ingredientIds: List<Long> = emptyList(),

    val steps: List<String> = emptyList()
)
