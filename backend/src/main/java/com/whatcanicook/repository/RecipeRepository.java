package com.whatcanicook.repository;

import com.whatcanicook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // recetas de un usuario
    List<Recipe> findByUserId(Long userId);

    // buscar recetas por nombre
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    // devuelve las recetas publicas ordenadas por fecha
    List<Recipe> findByPublicRecipeTrueOrderByCreatedAtDesc();

    @Query("""
            SELECT r FROM Recipe r
            JOIN r.ingredients i
            WHERE r.publicRecipe = true
              AND i.id IN :ingredientIds
            GROUP BY r.id
            HAVING COUNT(DISTINCT i.id) = :ingredientCount
            ORDER BY r.createdAt DESC
            """)
    List<Recipe> findPublicRecipesContainingAllIngredients(
            @Param("ingredientIds") List<Long> ingredientIds,
            @Param("ingredientCount") long ingredientCount
    );
}
