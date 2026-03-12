package com.whatcanicook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean publicRecipe = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipes_user"))
    @JsonIgnoreProperties({"recipes", "favoriteRecipes"})
    private User user;

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(
                    name = "recipe_id",
                    foreignKey = @ForeignKey(name = "fk_recipe_ingredients_recipe")),
            inverseJoinColumns = @JoinColumn(
                    name = "ingredient_id",
                    foreignKey = @ForeignKey(name = "fk_recipe_ingredients_ingredient")))
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<RecipeStep> steps = new ArrayList<>();

}
