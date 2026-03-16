package com.whatcanicook.controller;

import com.whatcanicook.dto.model.FavoriteRecipeDto;
import com.whatcanicook.dto.request.FavoriteRecipeRequest;
import com.whatcanicook.dto.response.ApiMessageResponse;
import com.whatcanicook.service.FavoriteRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteRecipeController {

    private final FavoriteRecipeService favoriteRecipeService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteRecipeDto>> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteRecipeService.getFavorites(userId));
    }

    @PostMapping
    public ResponseEntity<FavoriteRecipeDto> addFavorite(@RequestBody FavoriteRecipeRequest request) {
        return ResponseEntity.ok(favoriteRecipeService.addFavorite(request));
    }

    @DeleteMapping
    public ResponseEntity<ApiMessageResponse> removeFavorite(@RequestParam Long userId,
                                                             @RequestParam Long recipeId) {
        favoriteRecipeService.removeFavorite(userId, recipeId);
        return ResponseEntity.ok(new ApiMessageResponse(true, "Receta eliminada de favoritos"));
    }
}
