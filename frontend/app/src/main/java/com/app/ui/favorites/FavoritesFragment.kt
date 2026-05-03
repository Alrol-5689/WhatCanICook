package com.app.ui.favorites

import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.databinding.FragmentFavoritesBinding
import com.app.dto.request.FavoriteRecipeRequest
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.app.ui.recipes.adapter.RecipeAdapter
import com.app.R
import androidx.navigation.fragment.findNavController
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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

        viewModel.loadFavoriteRecipes(SessionManager.userId)
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipeId ->
                val bundle = Bundle().apply {
                    putLong("recipeId", recipeId)
                }
                findNavController().navigate(R.id.recipeDetailFragment, bundle)
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
            recipeAdapter.setFavoriteRecipeIds((recipes ?: emptyList()).map { it.id }.toSet())

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
                if (response.isSuccessful) {
                    viewModel.loadFavoriteRecipes(userId)
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar favoritas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
