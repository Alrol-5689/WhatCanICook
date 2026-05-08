package com.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.Normalizer
import com.app.R
import com.app.databinding.FragmentPantryBinding
import com.app.databinding.ItemPantryIngredientBinding
import com.app.dto.model.IngredientDto
import com.app.ui.recipes.adapter.IngredientSearchAdapter
import com.google.android.material.chip.Chip

class PantryFragment : Fragment() {

    private var _binding: FragmentPantryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PantryViewModel by viewModels()
    private lateinit var selectedAdapter: SelectedIngredientAdapter
    private lateinit var ingredientSearchAdapter: IngredientSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPantryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupInputs()
        observarViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { 
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        selectedAdapter = SelectedIngredientAdapter { ingredientId ->
            viewModel.removeIngredient(ingredientId)
        }
        binding.pantryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pantryRecyclerView.adapter = selectedAdapter

        ingredientSearchAdapter = IngredientSearchAdapter { ingredient ->
            viewModel.addIngredient(ingredient)
            binding.editSearchIngredient.text?.clear()
            binding.rvIngredientSearch.visibility = View.GONE
        }
        binding.rvIngredientSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredientSearch.adapter = ingredientSearchAdapter
    }

    private fun setupInputs() {
        binding.editSearchIngredient.doAfterTextChanged { editable ->
            val query = editable?.toString()?.trim()?.lowercase().orEmpty()
            viewModel.searchIngredients(query)
        }

        binding.editSearchIngredient.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                
                val name = v.text.toString().trim()
                if (name.isNotEmpty()) {
                    val available = viewModel.availableIngredients.value ?: emptyList()
                    val normalizedName = name.removeAccents().lowercase()
                    val existing = available.find { 
                        it.name.removeAccents().lowercase() == normalizedName || 
                        it.castellano?.removeAccents()?.lowercase() == normalizedName 
                    }
                    
                    if (existing != null) {
                        viewModel.addIngredient(existing)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.ingrediente_no_encontrado),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    
                    binding.editSearchIngredient.text?.clear()
                    binding.rvIngredientSearch.visibility = View.GONE
                }
                true
            } else {
                false
            }
        }

        binding.findRecipesButton.setOnClickListener {
            val ingredientIds = viewModel.getSelectedIngredientIds()
            if (ingredientIds.isEmpty()) return@setOnClickListener

            findNavController().navigate(R.id.action_pantry_to_results)
        }
    }

    private fun observarViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { list ->
            val selectedIds = (viewModel.selectedIngredients.value ?: emptyList()).map { it.id }.toSet()
            val filtered = (list ?: emptyList()).filter { it.id !in selectedIds }
            
            ingredientSearchAdapter.setIngredients(filtered)
            binding.rvIngredientSearch.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.selectedIngredients.observe(viewLifecycleOwner) { list ->
            selectedAdapter.updateList(list ?: emptyList())
            syncChipsSelection()
        }

        viewModel.error.observe(viewLifecycleOwner) { _ ->
            binding.rvIngredientSearch.visibility = View.GONE
        }

        viewModel.availableIngredients.observe(viewLifecycleOwner) { list ->
            actualizarChipsDisponibles(list ?: emptyList())
        }
    }

    private fun actualizarChipsDisponibles(disponibles: List<IngredientDto>) {
        binding.chipGroupAvailable.removeAllViews()
        val selectedIds = viewModel.getSelectedIngredientIds()

        // LIMITAMOS a 30 para no saturar el rendimiento de la UI (ChipGroup es lento)
        disponibles.take(30).forEach { ingredient ->
            val chip = Chip(requireContext()).apply {
                tag = ingredient.id
                val spanish = ingredient.castellano?.takeIf { it.isNotBlank() }
                text = spanish ?: ingredient.name
                isCheckable = true
                isChecked = selectedIds.contains(ingredient.id)
                setOnClickListener {
                    if (isChecked) {
                        viewModel.addIngredient(ingredient)
                    } else {
                        viewModel.removeIngredient(ingredient.id)
                    }
                }
            }
            binding.chipGroupAvailable.addView(chip)
        }
    }

    private fun syncChipsSelection() {
        val selectedIds = viewModel.getSelectedIngredientIds()
        for (i in 0 until binding.chipGroupAvailable.childCount) {
            val chip = binding.chipGroupAvailable.getChildAt(i) as? Chip
            val tagId = chip?.tag as? Long
            if (chip != null && tagId != null) {
                chip.isChecked = selectedIds.contains(tagId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SelectedIngredientAdapter(private val onRemove: (Long) -> Unit) :
        RecyclerView.Adapter<SelectedIngredientAdapter.ViewHolder>() {

        private var items: List<IngredientDto> = emptyList()

        fun updateList(newList: List<IngredientDto>) {
            items = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemPantryIngredientBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            val spanish = item.castellano?.takeIf { it.isNotBlank() }
            val english = item.name
            holder.binding.textIngredientName.text = when {
                spanish == null -> english
                spanish.equals(english, ignoreCase = true) -> spanish
                else -> "$spanish ($english)"
            }
            holder.binding.btnRemoveIngredient.setOnClickListener { onRemove(item.id) }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(val binding: ItemPantryIngredientBinding) : 
            RecyclerView.ViewHolder(binding.root)
    }

    private fun String.removeAccents(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(normalized, "")
    }
}
