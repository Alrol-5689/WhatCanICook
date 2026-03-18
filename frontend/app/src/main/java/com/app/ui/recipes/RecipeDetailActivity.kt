package com.app.ui.recipes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private val viewModel: RecipeDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos la flecha de volver del toolbar
        binding.toolbar.setNavigationOnClickListener {
            // Cierra esta pantalla y vuelve a la anterior
            finish()
        }

        val recipeId = intent.getLongExtra("recipeId", -1L)

        observarViewModel()

        if (recipeId != -1L) {
            viewModel.loadRecipeDetail(recipeId)
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
}