package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientService;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipeCreateDtoMapper implements Function<RecipeCreateDto, Recipe> {
  @Autowired private IngredientService ingredientService;

  @Override
  public Recipe apply(RecipeCreateDto recipeCreateDto) {
    boolean hasIngredientIds =
        !(recipeCreateDto.ingredientIds() == null || recipeCreateDto.ingredientIds().isEmpty());
    List<Ingredient> ingredients =
        hasIngredientIds
            ? recipeCreateDto.ingredientIds().stream()
                .map(id -> ingredientService.findById(id))
                .toList()
            : new ArrayList<>();
    return new Recipe(null, recipeCreateDto.name(), ingredients);
  }
}
