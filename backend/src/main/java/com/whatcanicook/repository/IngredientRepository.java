package com.whatcanicook.repository;

import com.whatcanicook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

//    Spring genera automáticamente:

//    save()
//    saveAll()
//    findAll()
//    findById()
//    delete()
//    count()

}
