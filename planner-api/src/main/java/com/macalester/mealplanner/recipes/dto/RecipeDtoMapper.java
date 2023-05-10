package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.dto.IngredientMapper;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class RecipeDtoMapper implements Function<Recipe, RecipeDto> {
  private final IngredientMapper ingredientMapper = Mappers.getMapper(IngredientMapper.class);

  @Override
  public RecipeDto apply(Recipe recipe) {
    List<Ingredient> sorted =
        recipe.getIngredients() == null
            ? List.of()
            : recipe.getIngredients().stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .toList();
    return new RecipeDto(
        recipe.getId(), recipe.getName(), ingredientMapper.ingredientToDto(sorted));
  }
}
