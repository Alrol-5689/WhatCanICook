package com.app.ui.recipes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.dto.model.RecipeSummaryDto

class RecipeAdapter(
    // Recibimos una función que se ejecutará cuando el usuario pulse una receta
    private val onRecipeClick: (Long) -> Unit,
    private val onFavoriteToggle: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // Lista de recetas que se van a mostrar en el RecyclerView
    private var recipes: List<RecipeSummaryDto> = emptyList()
    private val favoriteRecipeIds: MutableSet<Long> = mutableSetOf()

    // Actualiza la lista y refresca la UI
    fun setRecipes(recipes: List<RecipeSummaryDto>) {
        this.recipes = recipes
        notifyDataSetChanged() // -> más adelante lo hacemos con DiffUtil / ListAdapter
    }

    fun setFavoriteRecipeIds(ids: Set<Long>) {
        favoriteRecipeIds.clear()
        favoriteRecipeIds.addAll(ids)
        notifyDataSetChanged()
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

        val isFavorite = favoriteRecipeIds.contains(recipe.id)
        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )

        if (recipe.matchingIngredients != null && recipe.totalIngredients != null) {
            holder.textMatchStatus.visibility = View.VISIBLE
            if (recipe.matchingIngredients == recipe.totalIngredients) {
                holder.textMatchStatus.text = "¡Ideal!"
                holder.textMatchStatus.setBackgroundResource(R.color.pumpkin_orange)
            } else {
                val missing = recipe.totalIngredients - recipe.matchingIngredients
                holder.textMatchStatus.text = "Faltan $missing ingr."
                holder.textMatchStatus.setBackgroundResource(android.R.color.darker_gray)
            }
        } else {
            holder.textMatchStatus.visibility = View.GONE
        }

        // Detectamos click en la fila
        holder.itemView.setOnClickListener {
            onRecipeClick(recipe.id)
        }

        holder.favoriteButton.setOnClickListener {
            val nowFavorite = !favoriteRecipeIds.contains(recipe.id)
            if (nowFavorite) favoriteRecipeIds.add(recipe.id) else favoriteRecipeIds.remove(recipe.id)
            notifyItemChanged(position)
            onFavoriteToggle(recipe.id, nowFavorite)
        }
    }

    // Número de elementos en la lista
    override fun getItemCount(): Int = recipes.size

    // ViewHolder: guarda referencias a las vistas de cada item
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val textMatchStatus: TextView = itemView.findViewById(R.id.textMatchStatus)
    }
}
