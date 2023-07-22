package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientService;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipeCreateDtoMapper implements Function<RecipeCreateDto, Recipe> {
  @Autowired private IngredientService ingredientService;

  @Override
  public Recipe apply(RecipeCreateDto recipeCreateDto) {
    boolean hasIngredientIds =
        !(recipeCreateDto.ingredientIds() == null || recipeCreateDto.ingredientIds().isEmpty());
    Set<Ingredient> ingredients =
        hasIngredientIds
            ? recipeCreateDto.ingredientIds().stream()
                .map(id -> ingredientService.findById(id))
                .collect(Collectors.toSet())
            : new HashSet<>();
    return new Recipe(null, recipeCreateDto.name().trim().toLowerCase().replaceAll(" +", " "), ingredients);
  }
}
