package com.whatcanicook.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRecipeDto {

    private Long userId;

    private RecipeSummaryDto recipe;
}
