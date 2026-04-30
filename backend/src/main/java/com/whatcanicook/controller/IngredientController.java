package com.whatcanicook.controller;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getIngredients(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.ok(ingredientService.getAllIngredients());
        }

        return ResponseEntity.ok(ingredientService.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ResponseEntity.ok(ingredientService.createIngredient(ingredientDto));
    }
}
