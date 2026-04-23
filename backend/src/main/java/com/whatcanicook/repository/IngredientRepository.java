package com.whatcanicook.repository;

import com.whatcanicook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

//    Spring genera automáticamente:

//    save()
//    saveAll()
//    findAll()
//    findById()
//    delete()
//    count()

    // Buscar ingredientes por nombre original o nombre en castellano.
    List<Ingredient> findByNameContainingIgnoreCaseOrCastellanoContainingIgnoreCase(String name, String castellano);

    Optional<Ingredient> findFirstByNameIgnoreCaseOrCastellanoIgnoreCase(String name, String castellano);

}
