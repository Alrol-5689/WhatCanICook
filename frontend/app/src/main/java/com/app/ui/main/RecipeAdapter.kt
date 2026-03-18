package com.app.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.dto.model.RecipeSummaryDto

class RecipeAdapter(
    // Recibimos una función que se ejecutará cuando el usuario pulse una receta
    private val onRecipeClick: (Long) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // Lista de recetas que se van a mostrar en el RecyclerView
    private var recipes: List<RecipeSummaryDto> = emptyList()

    // Actualiza la lista y refresca la UI
    fun setRecipes(recipes: List<RecipeSummaryDto>) {
        this.recipes = recipes
        notifyDataSetChanged() // -> más adelante lo hacemos con DiffUtil / ListAdapter
    }

    // Se crea la vista de cada item (item_recipe.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    // Se rellenan los datos en cada fila
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Pintamos los datos
        holder.textTitle.text = recipe.title
        holder.textDescription.text = recipe.description
        holder.textUsername.text = holder.itemView.context.getString(
            R.string.recipe_author,
            recipe.username
        )

        // Detectamos click en la fila
        holder.itemView.setOnClickListener {
            onRecipeClick(recipe.id)
        }
    }

    // Número de elementos en la lista
    override fun getItemCount(): Int = recipes.size

    // ViewHolder: guarda referencias a las vistas de cada item
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
    }
}