package com.whatcanicook.controller;

import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.service.RecipeStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recipes/{recipeId}/steps")
@RequiredArgsConstructor
public class RecipeStepController {

    private final RecipeStepService recipeStepService;

    @GetMapping
    public ResponseEntity<List<RecipeStepDto>> getStepsByRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeStepService.getStepsByRecipe(recipeId));
    }
}
