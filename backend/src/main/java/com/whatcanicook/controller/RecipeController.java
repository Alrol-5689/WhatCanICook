package com.whatcanicook.controller;

import com.whatcanicook.dto.model.RecipeDto;
import com.whatcanicook.dto.request.CreateRecipeRequest;
import com.whatcanicook.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/public")
    public ResponseEntity<List<RecipeDto>> getPublicRecipes() {
        return ResponseEntity.ok(recipeService.getPublicRecipes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDto>> getRecipesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(recipeService.getRecipesByUser(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeDto>> searchRecipesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(recipeService.searchRecipesByTitle(title));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody CreateRecipeRequest request) {
        return ResponseEntity.ok(recipeService.createRecipe(request));
    }
}
