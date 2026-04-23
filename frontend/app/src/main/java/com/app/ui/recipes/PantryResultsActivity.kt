package com.app.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.databinding.ActivityPantryResultsBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.ui.recipes.adapter.RecipeAdapter
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryResultsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INGREDIENTS = "ingredients"
    }

    private lateinit var binding: ActivityPantryResultsBinding
    private val viewModel: PantryResultsViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantryResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        observeViewModel()

        val ingredients = intent.getStringArrayListExtra(EXTRA_INGREDIENTS) ?: arrayListOf()
        viewModel.loadRecipes(ingredients)
        refreshFavorites()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipeId ->
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra("recipeId", recipeId)
                startActivity(intent)
            },
            onFavoriteToggle = { recipeId, nowFavorite ->
                toggleFavorite(recipeId, nowFavorite)
            }
        )
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recipesRecyclerView.adapter = recipeAdapter
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(this) { recipes ->
            recipeAdapter.setRecipes(recipes ?: emptyList())
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshFavorites() {
        if (!SessionManager.isLoggedIn()) {
            recipeAdapter.setFavoriteRecipeIds(emptySet())
            return
        }

        RetrofitClient.favoriteApi.getUserFavorites(SessionManager.userId)
            .enqueue(object : Callback<List<com.app.dto.model.FavoriteRecipeDto>> {
                override fun onResponse(
                    call: Call<List<com.app.dto.model.FavoriteRecipeDto>>,
                    response: Response<List<com.app.dto.model.FavoriteRecipeDto>>
                ) {
                    if (response.isSuccessful) {
                        val ids = (response.body() ?: emptyList()).map { it.recipe.id }.toSet()
                        recipeAdapter.setFavoriteRecipeIds(ids)
                    }
                }

                override fun onFailure(call: Call<List<com.app.dto.model.FavoriteRecipeDto>>, t: Throwable) {}
            })
    }

    private fun toggleFavorite(recipeId: Long, nowFavorite: Boolean) {
        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(this, "Haz login para usar favoritos", Toast.LENGTH_SHORT).show()
            refreshFavorites()
            return
        }

        val userId = SessionManager.userId
        val call: Call<*> = if (nowFavorite) {
            RetrofitClient.favoriteApi.addFavorite(FavoriteRecipeRequest(userId, recipeId))
        } else {
            RetrofitClient.favoriteApi.removeFavorite(userId, recipeId)
        }

        @Suppress("UNCHECKED_CAST")
        (call as Call<Any>).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@PantryResultsActivity, "Error al actualizar favoritas", Toast.LENGTH_SHORT).show()
                    refreshFavorites()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@PantryResultsActivity, t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                refreshFavorites()
            }
        })
    }
}
