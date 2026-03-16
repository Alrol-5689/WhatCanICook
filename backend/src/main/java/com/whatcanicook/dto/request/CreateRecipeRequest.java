package com.whatcanicook.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequest {

    private String title;

    private String description;

    private boolean publicRecipe = true;

    private Long userId;

    private List<Long> ingredientIds;

    private List<String> steps;
}
