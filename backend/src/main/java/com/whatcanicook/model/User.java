package com.whatcanicook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50) // Estas validaciones funcionan usando @Valid en el controller
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "favorite_recipes",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(name = "fk_favorite_recipes_user")),
            inverseJoinColumns = @JoinColumn(
                    name = "recipe_id",
                    foreignKey = @ForeignKey(name = "fk_favorite_recipes_recipe")))
    private List<Recipe> favoriteRecipes = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "pantry_ingredients",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(name = "fk_pantry_ingredients_user")),
            inverseJoinColumns = @JoinColumn(
                    name = "ingredient_id",
                    foreignKey = @ForeignKey(name = "fk_pantry_ingredients_ingredient")))
    private List<Ingredient> pantryIngredients = new ArrayList<>();

//    User  ←→  Recipe
//
//    User 1 ─── N favorite_recipes N ─── 1 Recipe
//
//            favorite_recipes
//    ----------------
//    user_id      → FK a users.id
//    recipe_id    → FK a recipes.id


}
