package com.whatcanicook.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipesByIngredientIdsRequest {
    private List<Long> ingredientIds;
}

