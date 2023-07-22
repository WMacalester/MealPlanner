package com.macalester.mealplanner.ingredients.dto;

import com.macalester.mealplanner.validator.NameConstraint;

public record IngredientCreateDto(@NameConstraint String name) {}
