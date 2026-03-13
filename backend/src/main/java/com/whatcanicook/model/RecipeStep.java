package com.whatcanicook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "recipe_steps")
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipe_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recipe_steps_recipe"))
    private Recipe recipe;

    @Column(nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, length = 1000)
    private String description;
}
