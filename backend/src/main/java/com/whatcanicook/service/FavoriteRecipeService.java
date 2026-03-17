package com.whatcanicook.service;

import com.whatcanicook.dto.model.FavoriteRecipeDto;
import com.whatcanicook.dto.request.FavoriteRecipeRequest;
import com.whatcanicook.mapper.RecipeMapper;
import com.whatcanicook.model.Recipe;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.RecipeRepository;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteRecipeService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public FavoriteRecipeService(UserRepository userRepository,
                                 RecipeRepository recipeRepository,
                                 RecipeMapper recipeMapper) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
    }

    public List<FavoriteRecipeDto> getFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return user.getFavoriteRecipes()
                .stream()
                .map(recipe -> new FavoriteRecipeDto(user.getId(), recipeMapper.toSummaryDto(recipe)))
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

        return new FavoriteRecipeDto(user.getId(), recipeMapper.toSummaryDto(recipe));
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
}
