package com.whatcanicook.service;

import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.model.RecipeStep;
import com.whatcanicook.repository.RecipeStepRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;

    public RecipeStepService(RecipeStepRepository recipeStepRepository) {
        this.recipeStepRepository = recipeStepRepository;
    }

    public List<RecipeStepDto> getStepsByRecipe(Long recipeId) {
        return recipeStepRepository.findByRecipeIdOrderByStepNumberAsc(recipeId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private RecipeStepDto mapToDto(RecipeStep step) {
        return new RecipeStepDto(
                step.getId(),
                step.getStepNumber(),
                step.getDescription()
        );
    }
}
