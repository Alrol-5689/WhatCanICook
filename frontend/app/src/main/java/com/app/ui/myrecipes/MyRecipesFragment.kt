package com.app.ui.myrecipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.databinding.FragmentMyRecipesBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.app.ui.recipes.adapter.RecipeAdapter
import com.app.ui.recipes.RecipeDetailActivity
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyRecipesFragment : Fragment() {

    private var _binding: FragmentMyRecipesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyRecipesViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        setupRecyclerView()
        observeViewModel()

        if (!SessionManager.isLoggedIn()) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recipesRecyclerView.visibility = View.GONE
            return
        }

        viewModel.loadMyRecipes(SessionManager.userId)
        refreshFavorites()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipeId ->
                val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                intent.putExtra("recipeId", recipeId)
                startActivity(intent)
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
            recipeAdapter.setRecipes(recipes ?: emptyList())

            if (recipes.isNullOrEmpty()) {
                binding.textEmptyState.visibility = View.VISIBLE
                binding.recipesRecyclerView.visibility = View.GONE
            } else {
                binding.textEmptyState.visibility = View.GONE
                binding.recipesRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshFavorites() {
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
}
