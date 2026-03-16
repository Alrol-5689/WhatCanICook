package com.whatcanicook.repository;

import com.whatcanicook.model.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    List<RecipeStep> findByRecipeIdOrderByStepNumberAsc(Long recipeId);
}
