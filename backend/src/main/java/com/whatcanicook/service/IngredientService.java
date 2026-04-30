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

        return ingredientRepository.findByNameContainingIgnoreCaseOrCastellanoContainingIgnoreCase(name, name)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public IngredientDto createIngredient(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.getName());
        ingredient.setCastellano(dto.getCastellano());
        // Inicializar a 0 por defecto si no se proporcionan
        ingredient.setCarbs100g(dto.getCarbs100g() != null ? dto.getCarbs100g() : 0.0);
        ingredient.setProtein100g(dto.getProtein100g() != null ? dto.getProtein100g() : 0.0);
        ingredient.setFat100g(dto.getFat100g() != null ? dto.getFat100g() : 0.0);
        ingredient.setFiber100g(dto.getFiber100g() != null ? dto.getFiber100g() : 0.0);

        Ingredient saved = ingredientRepository.save(ingredient);
        return mapToDto(saved);
    }

    private IngredientDto mapToDto(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getCarbs100g(),
                ingredient.getProtein100g(),
                ingredient.getFat100g(),
                ingredient.getFiber100g(),
                ingredient.getCastellano()
        );
    }
}
