package com.whatcanicook.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {

    private Long id;

    private String name;

    private Double carbs100g;

    private Double protein100g;

    private Double fat100g;

    private Double fiber100g;
}
