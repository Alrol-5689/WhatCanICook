package com.app.ui.recipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.databinding.ItemIngredientSearchBinding
import com.app.dto.model.IngredientDto

class IngredientSearchAdapter(
    private val onIngredientClick: (IngredientDto) -> Unit
) : RecyclerView.Adapter<IngredientSearchAdapter.IngredientViewHolder>() {

    private var ingredients: List<IngredientDto> = emptyList()

    fun setIngredients(newIngredients: List<IngredientDto>) {
        this.ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding =
            ItemIngredientSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = ingredients[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = ingredients.size

    inner class IngredientViewHolder(private val binding: ItemIngredientSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: IngredientDto) {
            val spanish = ingredient.castellano?.takeIf { it.isNotBlank() }
            val english = ingredient.name
            binding.tvIngredientName.text = when {
                spanish == null -> english
                spanish.equals(english, ignoreCase = true) -> spanish
                else -> "$spanish ($english)"
            }

            binding.root.setOnClickListener {
                onIngredientClick(ingredient)
            }
        }
    }
}

