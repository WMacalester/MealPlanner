package com.macalester.mealplanner.ingredients.dto;

import jakarta.validation.constraints.Pattern;

public record IngredientEditDto(
    //        Name can be null or NotBlank
    @Pattern(regexp = "^(?!\s*$).+", message = "Name must not be blank") String name) {}
