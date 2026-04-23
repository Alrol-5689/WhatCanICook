package com.whatcanicook.config;

import com.whatcanicook.model.Ingredient;
import com.whatcanicook.repository.IngredientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        InputStream is = new ClassPathResource("data/ingredients.json").getInputStream();

        List<Ingredient> ingredients =
                objectMapper.readValue(is, new TypeReference<List<Ingredient>>() {});

        long existingCount = ingredientRepository.count();
        if (existingCount == 0) {
            ingredientRepository.saveAll(ingredients);
            System.out.println("Ingredientes cargados: " + ingredients.size());
            return;
        }

        List<Ingredient> existing = ingredientRepository.findAll();
        Map<String, Ingredient> existingByName = new HashMap<>(existing.size());
        for (Ingredient ingredient : existing) {
            if (ingredient.getName() == null) continue;
            existingByName.put(ingredient.getName().toLowerCase(Locale.ROOT), ingredient);
        }

        List<Ingredient> toSave = new ArrayList<>();
        int inserted = 0;
        int updated = 0;

        for (Ingredient seed : ingredients) {
            if (seed.getName() == null || seed.getName().isBlank()) continue;

            Ingredient current = existingByName.get(seed.getName().toLowerCase(Locale.ROOT));
            if (current == null) {
                seed.setId(null);
                toSave.add(seed);
                inserted++;
                continue;
            }

            boolean changed = false;

            if ((current.getCastellano() == null || current.getCastellano().isBlank())
                    && seed.getCastellano() != null
                    && !seed.getCastellano().isBlank()) {
                current.setCastellano(seed.getCastellano());
                changed = true;
            }

            if (current.getCarbs100g() == null && seed.getCarbs100g() != null) {
                current.setCarbs100g(seed.getCarbs100g());
                changed = true;
            }
            if (current.getProtein100g() == null && seed.getProtein100g() != null) {
                current.setProtein100g(seed.getProtein100g());
                changed = true;
            }
            if (current.getFat100g() == null && seed.getFat100g() != null) {
                current.setFat100g(seed.getFat100g());
                changed = true;
            }
            if (current.getFiber100g() == null && seed.getFiber100g() != null) {
                current.setFiber100g(seed.getFiber100g());
                changed = true;
            }

            if (changed) {
                toSave.add(current);
                updated++;
            }
        }

        if (!toSave.isEmpty()) {
            ingredientRepository.saveAll(toSave);
        }

        System.out.println("Ingredientes seed leídos: " + ingredients.size()
                + " | existentes: " + existingCount
                + " | insertados: " + inserted
                + " | actualizados: " + updated);
    }
}
