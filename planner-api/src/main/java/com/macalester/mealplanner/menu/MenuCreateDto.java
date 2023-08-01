package com.macalester.mealplanner.menu;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record MenuCreateDto(@NotNull(message = "{menu.error.nullSelectedRecipeIds}") Set<UUID> recipeIds) {}
