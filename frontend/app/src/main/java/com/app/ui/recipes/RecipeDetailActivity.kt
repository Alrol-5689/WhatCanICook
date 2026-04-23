package com.app.ui.recipes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.R
import com.app.databinding.ActivityRecipeDetailBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private val viewModel: RecipeDetailViewModel by viewModels()
    private var recipeId: Long = -1L
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos la flecha de volver del toolbar
        binding.toolbar.setNavigationOnClickListener {
            // Cierra esta pantalla y vuelve a la anterior
            finish()
        }

        binding.toolbar.inflateMenu(R.menu.recipe_detail_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_favorite) {
                toggleFavorite()
                true
            } else {
                false
            }
        }

        recipeId = intent.getLongExtra("recipeId", -1L)

        observarViewModel()

        if (recipeId != -1L) {
            viewModel.loadRecipeDetail(recipeId)
            refreshFavoriteState()
        } else {
            Toast.makeText(this, "Receta no válida", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observarViewModel() {
        viewModel.recipe.observe(this) { recipe ->
            binding.textTitle.text = recipe.title
            binding.textDescription.text = recipe.description
            binding.textUsername.text = getString(com.app.R.string.recipe_author, recipe.username)
            binding.textIngredients.text = recipe.ingredients.joinToString("\n") { "• ${it.name}" }
            binding.textSteps.text = recipe.steps.joinToString("\n") { "${it.stepNumber}. ${it.description}" }
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFavoriteIcon() {
        val item = binding.toolbar.menu.findItem(R.id.action_favorite) ?: return
        item.setIcon(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
    }

    private fun refreshFavoriteState() {
        if (!SessionManager.isLoggedIn() || recipeId == -1L) {
            isFavorite = false
            setFavoriteIcon()
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
                        isFavorite = ids.contains(recipeId)
                        setFavoriteIcon()
                    }
                }

                override fun onFailure(call: Call<List<com.app.dto.model.FavoriteRecipeDto>>, t: Throwable) {}
            })
    }

    private fun toggleFavorite() {
        if (!SessionManager.isLoggedIn() || recipeId == -1L) {
            Toast.makeText(this, "Haz login para usar favoritos", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = SessionManager.userId
        val call: Call<*> = if (isFavorite) {
            RetrofitClient.favoriteApi.removeFavorite(userId, recipeId)
        } else {
            RetrofitClient.favoriteApi.addFavorite(FavoriteRecipeRequest(userId, recipeId))
        }

        @Suppress("UNCHECKED_CAST")
        (call as Call<Any>).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    isFavorite = !isFavorite
                    setFavoriteIcon()
                } else {
                    Toast.makeText(this@RecipeDetailActivity, "Error al actualizar favoritas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@RecipeDetailActivity, t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
