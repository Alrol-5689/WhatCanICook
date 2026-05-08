package com.app.network

import com.app.dto.model.IngredientDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IngredientApi {

    @GET("ingredients")
    fun getAllIngredients(): Call<List<IngredientDto>>

    @GET("ingredients")
    fun searchIngredients(@Query("name") name: String): Call<List<IngredientDto>>

}
