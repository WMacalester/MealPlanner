package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.recipes.DietType;
import java.util.List;
import java.util.UUID;

public record RecipeDto(UUID id, String name, DietType dietType, List<IngredientDto> ingredients) {}
