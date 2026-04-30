package com.whatcanicook.mapper;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.dto.model.RecipeSummaryDto;
import com.whatcanicook.dto.model.RecipeDetailDto;
import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.model.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecipeMapper {

    private final IngredientMapper ingredientMapper;
    private final RecipeStepMapper recipeStepMapper;

    public RecipeMapper(IngredientMapper ingredientMapper, RecipeStepMapper recipeStepMapper) {
        this.ingredientMapper = ingredientMapper;
        this.recipeStepMapper = recipeStepMapper;
    }

    public RecipeSummaryDto toSummaryDto(Recipe recipe) {
        return new RecipeSummaryDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.isPublicRecipe(),
                recipe.getUser().getId(),
                recipe.getUser().getUsername(),
                recipe.getCreatedAt(),
                null,
                null
        );
    }

    public RecipeDetailDto toDetailDto(Recipe recipe) {
        List<IngredientDto> ingredientDtos = recipe.getIngredients()
                .stream()
                .map(ingredientMapper::toDto)
                .toList();

        List<RecipeStepDto> stepDtos = recipe.getSteps()
                .stream()
                .map(recipeStepMapper::toDto)
                .toList();

        return new RecipeDetailDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.isPublicRecipe(),
                recipe.getUser().getId(),
                recipe.getUser().getUsername(),
                recipe.getCreatedAt(),
                ingredientDtos,
                stepDtos
        );
    }
}
