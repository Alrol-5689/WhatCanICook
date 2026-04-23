package com.app.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.databinding.ActivityPantryBinding
import com.app.databinding.ItemPantryIngredientBinding
import com.app.dto.model.IngredientDto
import com.app.ui.recipes.adapter.IngredientSearchAdapter

class PantryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantryBinding
    private val viewModel: PantryViewModel by viewModels()
    private lateinit var selectedAdapter: SelectedIngredientAdapter
    private lateinit var ingredientSearchAdapter: IngredientSearchAdapter

    private var availableIngredients: List<IngredientDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupInputs()
        observarViewModel()

        viewModel.fetchIngredients()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        selectedAdapter = SelectedIngredientAdapter { ingredientId ->
            viewModel.removeIngredient(ingredientId)
        }
        binding.pantryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pantryRecyclerView.adapter = selectedAdapter

        ingredientSearchAdapter = IngredientSearchAdapter { ingredient ->
            viewModel.addIngredient(ingredient)
            binding.editSearchIngredient.text?.clear()
            binding.rvIngredientSearch.visibility = View.GONE
        }
        binding.rvIngredientSearch.layoutManager = LinearLayoutManager(this)
        binding.rvIngredientSearch.adapter = ingredientSearchAdapter
    }

    private fun setupInputs() {
        binding.editSearchIngredient.doAfterTextChanged { editable ->
            val query = editable?.toString()?.trim()?.lowercase().orEmpty()
            if (query.isEmpty()) {
                binding.rvIngredientSearch.visibility = View.GONE
                return@doAfterTextChanged
            }

            val selectedIds = (viewModel.selectedIngredients.value ?: emptyList()).map { it.id }.toSet()
            val filtered = availableIngredients
                .asSequence()
                .filter { it.id !in selectedIds }
                .filter {
                    (it.castellano?.lowercase()?.contains(query) == true) ||
                        it.name.lowercase().contains(query)
                }
                .take(25)
                .toList()

            ingredientSearchAdapter.setIngredients(filtered)
            binding.rvIngredientSearch.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.findRecipesButton.setOnClickListener {
            val ingredientIds = viewModel.getSelectedIngredientIds()
            val intent = Intent(this, PantryResultsActivity::class.java)
            intent.putExtra(PantryResultsActivity.EXTRA_INGREDIENT_IDS, ingredientIds.toLongArray())
            startActivity(intent)
        }
    }

    private fun observarViewModel() {
        viewModel.availableIngredients.observe(this) { list ->
            availableIngredients = list ?: emptyList()
        }

        viewModel.selectedIngredients.observe(this) { list ->
            selectedAdapter.updateList(list ?: emptyList())
        }

        viewModel.error.observe(this) { _ ->
            // Silencioso: si falla, simplemente no habrá sugerencias.
        }
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
}
