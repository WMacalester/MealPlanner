package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.recipes.DietType;
import com.macalester.mealplanner.validator.NameConstraint;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record RecipeCreateDto(@NameConstraint String name, @NotNull DietType dietType, List<UUID> ingredientIds) {}
