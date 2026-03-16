package com.whatcanicook.service;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.dto.model.RecipeDto;
import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.dto.request.CreateRecipeRequest;
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

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository,
                         UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }

    public List<RecipeDto> getPublicRecipes() {
        return recipeRepository.findByPublicRecipeTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<RecipeDto> getRecipesByUser(Long userId) {
        return recipeRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<RecipeDto> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public RecipeDto getRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        return mapToDto(recipe);
    }

    public RecipeDto createRecipe(CreateRecipeRequest request) {
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
        return mapToDto(saved);
    }

    private RecipeDto mapToDto(Recipe recipe) {
        List<IngredientDto> ingredientDtos = recipe.getIngredients()
                .stream()
                .map(this::mapIngredientToDto)
                .toList();

        List<RecipeStepDto> stepDtos = recipe.getSteps()
                .stream()
                .map(this::mapStepToDto)
                .toList();

        return new RecipeDto(
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

    private IngredientDto mapIngredientToDto(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getCarbs100g(),
                ingredient.getProtein100g(),
                ingredient.getFat100g(),
                ingredient.getFiber100g()
        );
    }

    private RecipeStepDto mapStepToDto(RecipeStep step) {
        return new RecipeStepDto(
                step.getId(),
                step.getStepNumber(),
                step.getDescription()
        );
    }
}
