package com.whatcanicook.config;

import com.whatcanicook.model.Ingredient;
import com.whatcanicook.repository.IngredientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
public class IngredientSeeder implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper;

    public IngredientSeeder(IngredientRepository ingredientRepository, ObjectMapper objectMapper) {
        this.ingredientRepository = ingredientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (ingredientRepository.count() > 0) {
            return;
        }

        InputStream is = new ClassPathResource("data/ingredients.json").getInputStream();

        List<Ingredient> ingredients =
                objectMapper.readValue(is, new TypeReference<List<Ingredient>>() {});

        ingredientRepository.saveAll(ingredients);

        System.out.println("Ingredientes cargados: " + ingredients.size());
    }
}
