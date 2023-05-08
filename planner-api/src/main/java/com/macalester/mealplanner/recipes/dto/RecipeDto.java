package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import java.util.List;
import java.util.UUID;

public record RecipeDto(UUID id, String name, List<IngredientDto> ingredients) {}
