package com.app.network

import android.util.Log
import com.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TAG = "RetrofitClient"

    private val retrofit: Retrofit by lazy {
        Log.d(TAG, "Inicializando Retrofit con BASE_URL: ${BuildConfig.BASE_URL}")

        val logging = HttpLoggingInterceptor { message -> 
            Log.d("API_NETWORK", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val recipeApi: RecipeApi by lazy { retrofit.create(RecipeApi::class.java) }
    val favoriteApi: FavoriteApi by lazy { retrofit.create(FavoriteApi::class.java) }
    val friendApi: FriendApi by lazy { retrofit.create(FriendApi::class.java) }
    val ingredientApi: IngredientApi by lazy { retrofit.create(IngredientApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }

}
