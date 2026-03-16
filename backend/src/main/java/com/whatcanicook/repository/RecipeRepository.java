package com.whatcanicook.repository;

import com.whatcanicook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // recetas de un usuario
    List<Recipe> findByUserId(Long userId);

    // buscar recetas por nombre
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    // devuelve las recetas publicas ordenadas por fecha
    List<Recipe> findByPublicRecipeTrueOrderByCreatedAtDesc();
}
