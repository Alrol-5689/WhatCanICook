package com.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.databinding.ActivityPantryBinding
import com.app.databinding.ItemPantryIngredientBinding

class PantryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantryBinding
    private val viewModel: PantryViewModel by viewModels()
    private lateinit var adapter: PantryAdapter

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
        adapter = PantryAdapter { ingredient ->
            viewModel.removeIngredient(ingredient)
        }
        binding.pantryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pantryRecyclerView.adapter = adapter
    }

    private fun setupInputs() {
        // Al pulsar ENTER en el teclado, añade el ingrediente
        binding.editSearchIngredient.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                val name = binding.editSearchIngredient.text.toString()
                viewModel.addIngredient(name)
                binding.editSearchIngredient.text.clear()
                true
            } else {
                false
            }
        }

        binding.findRecipesButton.setOnClickListener {
            viewModel.findRecipes()
            // Aquí podrías navegar a la pantalla de resultados
        }
    }

    private fun observarViewModel() {
        viewModel.ingredients.observe(this) { list ->
            adapter.updateList(list)
        }
    }

    // Clase interna para el adaptador de la lista de ingredientes
    inner class PantryAdapter(private val onRemove: (String) -> Unit) : 
        RecyclerView.Adapter<PantryAdapter.ViewHolder>() {

        private var items: List<String> = emptyList()

        fun updateList(newList: List<String>) {
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
            holder.binding.textIngredientName.text = item
            holder.binding.btnRemoveIngredient.setOnClickListener { onRemove(item) }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(val binding: ItemPantryIngredientBinding) : 
            RecyclerView.ViewHolder(binding.root)
    }
}