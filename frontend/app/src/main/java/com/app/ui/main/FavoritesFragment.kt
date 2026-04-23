package com.app.ui.main

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
import com.app.ui.recipes.RecipeDetailActivity
import com.app.utils.SessionManager

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
        recipeAdapter = RecipeAdapter { recipeId ->
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipeId)
            startActivity(intent)
        }
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
}
