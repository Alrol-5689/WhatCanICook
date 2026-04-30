package com.whatcanicook.service;

import com.whatcanicook.dto.model.RecipeSummaryDto;
import com.whatcanicook.dto.model.RecipeDetailDto;
import com.whatcanicook.dto.request.CreateRecipeRequest;
import com.whatcanicook.dto.request.RecipesByIngredientIdsRequest;
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
import java.util.Objects;

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

        List<Recipe> candidateRecipes = recipeRepository.findPublicRecipesWithAnyIngredient(ingredientIds);
        
        return processAndSortCandidateRecipes(candidateRecipes, ingredientIds);
    }

    public List<RecipeSummaryDto> findPublicRecipesByIngredientIds(RecipesByIngredientIdsRequest request) {
        List<Long> raw = request.getIngredientIds() != null ? request.getIngredientIds() : List.of();
        List<Long> normalized = raw.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (normalized.isEmpty()) {
            return List.of();
        }

        List<Recipe> candidateRecipes = recipeRepository.findPublicRecipesWithAnyIngredient(normalized);
        return processAndSortCandidateRecipes(candidateRecipes, normalized);
    }

    private List<RecipeSummaryDto> processAndSortCandidateRecipes(List<Recipe> recipes, List<Long> userIngredientIds) {
        return recipes.stream().map(recipe -> {
            RecipeSummaryDto dto = recipeMapper.toSummaryDto(recipe);
            long matchCount = recipe.getIngredients().stream()
                    .filter(i -> userIngredientIds.contains(i.getId()))
                    .count();
            dto.setMatchingIngredients((int) matchCount);
            dto.setTotalIngredients(recipe.getIngredients().size());
            return dto;
        }).sorted((r1, r2) -> {
            // Ordenar por:
            // 1. Es IDEAL primero (matching == total) vs no ideal
            boolean r1Ideal = r1.getMatchingIngredients().equals(r1.getTotalIngredients());
            boolean r2Ideal = r2.getMatchingIngredients().equals(r2.getTotalIngredients());
            if (r1Ideal && !r2Ideal) return -1;
            if (!r1Ideal && r2Ideal) return 1;

            // 2. Si ambos son igual, ordenar por porcentaje de match (descendente)
            double r1Ratio = (double) r1.getMatchingIngredients() / r1.getTotalIngredients();
            double r2Ratio = (double) r2.getMatchingIngredients() / r2.getTotalIngredients();
            int ratioCompare = Double.compare(r2Ratio, r1Ratio);
            if (ratioCompare != 0) return ratioCompare;

            // 3. Ordenar por fecha desc
            return r2.getCreatedAt().compareTo(r1.getCreatedAt());
        }).toList();
    }
}
