package com.macalester.mealplanner.recipes.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record RecipeCreateDto(@NotBlank String name, List<UUID> ingredientIds) {}
