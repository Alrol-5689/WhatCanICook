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
import com.google.android.material.chip.Chip
import android.widget.EditText
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog

class PantryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantryBinding
    private val viewModel: PantryViewModel by viewModels()
    private lateinit var selectedAdapter: SelectedIngredientAdapter
    private lateinit var ingredientSearchAdapter: IngredientSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupInputs()
        observarViewModel()
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
            // Ahora la búsqueda es en tiempo real consultando a la base de datos
            viewModel.searchIngredients(query)
        }

        binding.editSearchIngredient.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                
                val name = v.text.toString().trim()
                if (name.isNotEmpty()) {
                    val available = viewModel.availableIngredients.value ?: emptyList()
                    val existing = available.find { 
                        it.name.equals(name, ignoreCase = true) || 
                        it.castellano.equals(name, ignoreCase = true) 
                    }
                    
                    if (existing != null) {
                        viewModel.addIngredient(existing)
                    } else {
                        viewModel.createNewIngredient(name)
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
            
            val intent = Intent(this, PantryResultsActivity::class.java)
            intent.putExtra(PantryResultsActivity.EXTRA_INGREDIENT_IDS, ingredientIds.toLongArray())
            startActivity(intent)
        }

        binding.btnCreateIngredient.setOnClickListener {
            mostrarDialogoCrearIngrediente()
        }
    }

    private fun mostrarDialogoCrearIngrediente() {
        val editText = EditText(this).apply {
            hint = "Nombre del ingrediente"
            setPadding(50, 50, 50, 50)
        }
        AlertDialog.Builder(this)
            .setTitle("Crear Ingrediente")
            .setMessage("Añade un nuevo ingrediente a la base de datos global.")
            .setView(editText)
            .setPositiveButton("Crear") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createNewIngredient(name)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observarViewModel() {
        // Observamos los resultados de la búsqueda que vienen de la API
        viewModel.searchResults.observe(this) { list ->
            val selectedIds = (viewModel.selectedIngredients.value ?: emptyList()).map { it.id }.toSet()
            // Filtramos los que ya están seleccionados
            val filtered = (list ?: emptyList()).filter { it.id !in selectedIds }
            
            ingredientSearchAdapter.setIngredients(filtered)
            binding.rvIngredientSearch.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.selectedIngredients.observe(this) { list ->
            selectedAdapter.updateList(list ?: emptyList())
        }

        viewModel.error.observe(this) { _ ->
            binding.rvIngredientSearch.visibility = View.GONE
        }

        viewModel.availableIngredients.observe(this) { list ->
            actualizarChipsDisponibles(list ?: emptyList())
        }
    }

    private fun actualizarChipsDisponibles(disponibles: List<IngredientDto>) {
        binding.chipGroupAvailable.removeAllViews()
        val selectedIds = viewModel.getSelectedIngredientIds()

        disponibles.forEach { ingredient ->
            val chip = Chip(this).apply {
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
