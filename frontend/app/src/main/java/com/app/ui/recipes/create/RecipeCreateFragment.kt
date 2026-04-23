package com.app.ui.recipes.create

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.R
import com.app.databinding.FragmentRecipeCreateBinding
import com.app.dto.model.IngredientDto
import com.app.ui.recipes.adapter.IngredientSearchAdapter
import com.app.ui.recipes.adapter.StepAdapter
import com.app.utils.SessionManager

class RecipeCreateFragment : Fragment(R.layout.fragment_recipe_create) {
    private var _binding: FragmentRecipeCreateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeCreateViewModel by viewModels()
    private lateinit var stepAdapter: StepAdapter
    private lateinit var ingredientSearchAdapter: IngredientSearchAdapter

    private var availableIngredients = listOf<IngredientDto>()
    private val selectedIngredientIds = mutableSetOf<Long>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecipeCreateBinding.bind(view)

        setupRecyclerView()
        observarViewModel()
        acciones()

        viewModel.fetchIngredients()
    }

    private fun setupRecyclerView() {
        stepAdapter = StepAdapter()
        binding.rvSteps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSteps.adapter = stepAdapter

        ingredientSearchAdapter = IngredientSearchAdapter { ingredient ->
            selectedIngredientIds.add(ingredient.id)
            binding.editIngredientSearch.text.clear()
            binding.rvIngredientSearch.visibility = View.GONE
            updateSelectedIngredientsText()
        }
        binding.rvIngredientSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredientSearch.adapter = ingredientSearchAdapter
    }

    private fun observarViewModel() {
        viewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            availableIngredients = ingredients ?: emptyList()
            selectedIngredientIds.clear()
            updateSelectedIngredientsText()
        }

        viewModel.createdRecipe.observe(viewLifecycleOwner) { recipe ->
            Toast.makeText(
                requireContext(),
                "Receta creada: ${recipe.title}",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigateUp()
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSelectedIngredientsText() {
        if (selectedIngredientIds.isEmpty()) {
            binding.tvSelectedIngredients.text = "Ningún ingrediente seleccionado"
        } else {
            val names = availableIngredients
                .filter { selectedIngredientIds.contains(it.id) }
                .map { it.castellano?.takeIf(String::isNotBlank) ?: it.name }
            binding.tvSelectedIngredients.text = names.joinToString("\n- ", prefix = "- ")
        }
    }

    private fun acciones() {
        binding.editIngredientSearch.doAfterTextChanged { editable ->
            val query = editable?.toString()?.trim()?.lowercase().orEmpty()
            if (query.isEmpty()) {
                binding.rvIngredientSearch.visibility = View.GONE
            } else {
                val filtered = availableIngredients.filter {
                    (it.castellano?.lowercase()?.contains(query) == true) ||
                        (it.name.lowercase().contains(query))
                }
                ingredientSearchAdapter.setIngredients(filtered)
                binding.rvIngredientSearch.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        binding.btnClearIngredients.setOnClickListener {
            selectedIngredientIds.clear()
            updateSelectedIngredientsText()
        }

        binding.btnAddStep.setOnClickListener {
            stepAdapter.addStep()
        }

        binding.buttonCreateRecipe.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val description = binding.editDescription.text.toString()
            val publicRecipe = binding.checkPublicRecipe.isChecked
            val userId = SessionManager.userId

            viewModel.createRecipe(
                title = title,
                description = description,
                publicRecipe = publicRecipe,
                userId = userId,
                ingredientIds = selectedIngredientIds.toList(),
                steps = stepAdapter.steps.toList()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

