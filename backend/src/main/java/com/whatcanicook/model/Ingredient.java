package com.whatcanicook.model;

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

    @Column(name = "carbs_100g")
    private Double carbs100g;

    @Column(name = "protein_100g")
    private Double protein100g;

    @Column(name = "fat_100g")
    private Double fat100g;

    @Column(name = "fiber_100g")
    private Double fiber100g;
}
