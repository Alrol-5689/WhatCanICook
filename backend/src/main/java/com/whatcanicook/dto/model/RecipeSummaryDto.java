package com.whatcanicook.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSummaryDto {

    private Long id;

    private String title;

    private String description;

    private boolean publicRecipe;

    private Long userId;

    private String username;

    private LocalDateTime createdAt;

    private Integer matchingIngredients;

    private Integer totalIngredients;
}
