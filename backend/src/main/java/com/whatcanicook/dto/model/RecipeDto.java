package com.whatcanicook.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {

    private Long id;

    private String title;

    private String description;

    private boolean publicRecipe;

    private Long userId;

    private String username;

    private LocalDateTime createdAt;

    private List<IngredientDto> ingredients;

    private List<RecipeStepDto> steps;
}
