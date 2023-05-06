package com.macalester.mealplanner.ingredients.dto;

import jakarta.validation.constraints.NotBlank;

public record IngredientCreateDto(@NotBlank(message = "Name must not be blank") String name) {}
