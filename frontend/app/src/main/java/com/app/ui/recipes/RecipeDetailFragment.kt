package com.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import com.app.R
import com.app.databinding.FragmentRecipeDetailBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeDetailViewModel by viewModels()
    private var recipeId: Long = -1L
    private var isFavorite: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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

        recipeId = arguments?.getLong("recipeId") ?: -1L

        observarViewModel()

        if (recipeId != -1L) {
            viewModel.loadRecipeDetail(recipeId)
            refreshFavoriteState()
        } else {
            Toast.makeText(requireContext(), "Receta no válida", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.buttonEditRecipe.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("recipeId", recipeId)
            }
            findNavController().navigate(R.id.recipeCreateFragment, bundle)
        }

        binding.buttonDeleteRecipe.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Receta")
                .setMessage("¿Estás seguro de que quieres eliminar esta receta? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.deleteRecipe(recipeId, SessionManager.userId)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun observarViewModel() {
        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            binding.textTitle.text = recipe.title
            binding.textDescription.text = recipe.description
            binding.textUsername.text = getString(R.string.recipe_author, recipe.username)
            binding.textIngredients.text = recipe.ingredients.joinToString("\n") { "• ${it.name}" }
            binding.textSteps.text = recipe.steps.joinToString("\n") { "${it.stepNumber}. ${it.description}" }

            if (recipe.userId == SessionManager.userId) {
                binding.layoutActionButtons.visibility = View.VISIBLE
            } else {
                binding.layoutActionButtons.visibility = View.GONE
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Haz login para usar favoritos", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Error al actualizar favoritas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
