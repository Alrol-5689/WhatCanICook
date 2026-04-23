package com.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.app.BuildConfig

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: Retrofit
        get() = retrofit

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val recipeApi: RecipeApi by lazy {
        retrofit.create(RecipeApi::class.java)
    }

    val favoriteApi: FavoriteApi by lazy {
        retrofit.create(FavoriteApi::class.java)
    }

    val friendApi: FriendApi by lazy {
        retrofit.create(FriendApi::class.java)
    }

    val ingredientApi: IngredientApi by lazy {
        retrofit.create(IngredientApi::class.java)
    }

}
