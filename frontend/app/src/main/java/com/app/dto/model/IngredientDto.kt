package com.app.dto.model

data class IngredientDto(

    val id: Long,
    val name: String,

    val carbs100g: Double?,
    val protein100g: Double?,
    val fat100g: Double?,
    val fiber100g: Double?

)