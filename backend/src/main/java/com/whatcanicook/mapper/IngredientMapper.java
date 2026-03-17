package com.whatcanicook.mapper;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.model.Ingredient;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {


    public IngredientDto toDto(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getCarbs100g(),
                ingredient.getProtein100g(),
                ingredient.getFat100g(),
                ingredient.getFiber100g()
        );
    }
}
