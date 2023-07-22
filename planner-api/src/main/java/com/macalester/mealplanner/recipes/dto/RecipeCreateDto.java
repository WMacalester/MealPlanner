package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.validator.NameConstraint;
import java.util.List;
import java.util.UUID;

public record RecipeCreateDto(@NameConstraint String name, List<UUID> ingredientIds) {}
