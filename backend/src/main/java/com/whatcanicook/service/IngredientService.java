package com.whatcanicook.service;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.model.Ingredient;
import com.whatcanicook.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<IngredientDto> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllIngredients();
        }

        return ingredientRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private IngredientDto mapToDto(Ingredient ingredient) {
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
