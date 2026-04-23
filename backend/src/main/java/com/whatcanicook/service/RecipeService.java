package com.whatcanicook.service;

import com.whatcanicook.dto.model.RecipeSummaryDto;
import com.whatcanicook.dto.model.RecipeDetailDto;
import com.whatcanicook.dto.request.CreateRecipeRequest;
import com.whatcanicook.dto.request.RecipesByIngredientsRequest;
import com.whatcanicook.mapper.RecipeMapper;
import com.whatcanicook.model.Ingredient;
import com.whatcanicook.model.Recipe;
import com.whatcanicook.model.RecipeStep;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.IngredientRepository;
import com.whatcanicook.repository.RecipeRepository;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository,
                         UserRepository userRepository,
                         RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.recipeMapper = recipeMapper;
    }

    public List<RecipeSummaryDto> getPublicRecipes() {
        return recipeRepository.findByPublicRecipeTrueOrderByCreatedAtDesc()
                .stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
    }

    public List<RecipeSummaryDto> getRecipesByUser(Long userId) {
        return recipeRepository.findByUserId(userId)
                .stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
    }

    public List<RecipeSummaryDto> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
    }

    public RecipeDetailDto getRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        return recipeMapper.toDetailDto(recipe);
    }

    public RecipeDetailDto createRecipe(CreateRecipeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Long> ingredientIds = request.getIngredientIds() != null ? request.getIngredientIds() : List.of();
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);

        if (ingredientIds.size() != ingredients.size()) {
            throw new IllegalArgumentException("Algún ingrediente no existe");
        }

        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .publicRecipe(request.isPublicRecipe())
                .user(user)
                .ingredients(new ArrayList<>(ingredients))
                .build();

        List<String> stepDescriptions = request.getSteps() != null ? request.getSteps() : List.of();
        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < stepDescriptions.size(); i++) {
            steps.add(RecipeStep.builder()
                    .recipe(recipe)
                    .stepNumber(i + 1)
                    .description(stepDescriptions.get(i))
                    .build());
        }
        recipe.setSteps(steps);

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toDetailDto(saved);
    }

    public List<RecipeSummaryDto> findPublicRecipesByIngredients(RecipesByIngredientsRequest request) {
        List<String> raw = request.getIngredients() != null ? request.getIngredients() : List.of();
        List<String> normalized = raw.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (normalized.isEmpty()) {
            return List.of();
        }

        List<Long> ingredientIds = new ArrayList<>(normalized.size());
        for (String name : normalized) {
            var ingredient = ingredientRepository.findFirstByNameIgnoreCaseOrCastellanoIgnoreCase(name, name)
                    .orElse(null);
            if (ingredient == null) {
                // Si un ingrediente no existe, no puede haber recetas que contengan "todos".
                return List.of();
            }
            ingredientIds.add(ingredient.getId());
        }

        return recipeRepository.findPublicRecipesContainingAllIngredients(ingredientIds, ingredientIds.size())
                .stream()
                .map(recipeMapper::toSummaryDto)
                .toList();
    }
}
