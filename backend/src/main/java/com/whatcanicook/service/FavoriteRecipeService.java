package com.whatcanicook.service;

import com.whatcanicook.dto.model.FavoriteRecipeDto;
import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.dto.model.RecipeDto;
import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.dto.request.FavoriteRecipeRequest;
import com.whatcanicook.model.Ingredient;
import com.whatcanicook.model.Recipe;
import com.whatcanicook.model.RecipeStep;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.RecipeRepository;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteRecipeService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public FavoriteRecipeService(UserRepository userRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<FavoriteRecipeDto> getFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return user.getFavoriteRecipes()
                .stream()
                .map(recipe -> mapToFavoriteDto(user.getId(), recipe))
                .toList();
    }

    public FavoriteRecipeDto addFavorite(FavoriteRecipeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        boolean alreadyFavorite = user.getFavoriteRecipes()
                .stream()
                .anyMatch(r -> r.getId().equals(recipe.getId()));

        if (alreadyFavorite) {
            throw new IllegalArgumentException("La receta ya está en favoritos");
        }

        user.getFavoriteRecipes().add(recipe);
        userRepository.save(user);

        return mapToFavoriteDto(user.getId(), recipe);
    }

    public void removeFavorite(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean removed = user.getFavoriteRecipes()
                .removeIf(recipe -> recipe.getId().equals(recipeId));

        if (!removed) {
            throw new IllegalArgumentException("La receta no está en favoritos");
        }

        userRepository.save(user);
    }

    private FavoriteRecipeDto mapToFavoriteDto(Long userId, Recipe recipe) {
        return new FavoriteRecipeDto(userId, mapToDto(recipe));
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
