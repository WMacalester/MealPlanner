package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.dto.IngredientDtoMapper;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeDtoMapper implements Function<Recipe, RecipeDto> {
  private final IngredientDtoMapper ingredientDtoMapper;

  @Override
  public RecipeDto apply(Recipe recipe) {
    List<Ingredient> sorted =
        recipe.getIngredients() == null
            ? List.of()
            : recipe.getIngredients().stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .toList();
    return new RecipeDto(
        recipe.getId(), recipe.getName(), sorted.stream().map(ingredientDtoMapper).toList());
  }
}
