package com.whatcanicook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @JsonProperty("carbs_100g")
    @Column(name = "carbs_100g")
    private Double carbs100g;

    @JsonProperty("protein_100g")
    @Column(name = "protein_100g")
    private Double protein100g;

    @JsonProperty("fat_100g")
    @Column(name = "fat_100g")
    private Double fat100g;

    @JsonProperty("fiber_100g")
    @Column(name = "fiber_100g")
    private Double fiber100g;

    @Column(length = 255)
    private String castellano;
}
