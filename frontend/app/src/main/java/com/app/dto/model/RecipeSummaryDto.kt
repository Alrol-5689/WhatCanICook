package com.app.dto.model

data class RecipeSummaryDto(

    val id: Long,
    val title: String,
    val description: String,
    val publicRecipe: Boolean,
    val userId: Long,
    val username: String,
    val createdAt: String

)
