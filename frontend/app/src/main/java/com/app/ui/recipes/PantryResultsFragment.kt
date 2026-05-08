package com.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.R
import com.app.databinding.FragmentPantryResultsBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.ui.recipes.adapter.RecipeAdapter
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryResultsFragment : Fragment() {

    private var _binding: FragmentPantryResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PantryResultsViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPantryResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setupRecyclerView()
        observeViewModel()

        viewModel.loadRecipesFromPantry()
        refreshFavorites()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipeId ->
                val bundle = Bundle().apply {
                    putLong("recipeId", recipeId)
                }
                findNavController().navigate(R.id.action_pantry_results_to_detail, bundle)
            },
            onFavoriteToggle = { recipeId, nowFavorite ->
                toggleFavorite(recipeId, nowFavorite)
            }
        )
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerView.adapter = recipeAdapter
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            // Sort recipes: IDEAL first, then SUGGESTED.
            // Ideal if matchingIngredients >= totalIngredients
            val sorted = (recipes ?: emptyList()).sortedByDescending { recipe ->
                val match = recipe.matchingIngredients ?: 0
                val total = recipe.totalIngredients ?: 1 // avoid div by zero, though unlikely
                if (match >= total) 1 else 0 // 1 for Ideal, 0 for Suggested
            }
            recipeAdapter.setRecipes(sorted)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Haz login para usar favoritos", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Error al actualizar favoritas", Toast.LENGTH_SHORT).show()
                    refreshFavorites()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                refreshFavorites()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
