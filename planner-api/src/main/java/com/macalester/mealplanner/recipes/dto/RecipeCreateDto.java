package com.macalester.mealplanner.recipes.dto;

import java.util.List;
import java.util.UUID;

public record RecipeCreateDto(String name, List<UUID> ingredientIds) {}
