package com.macalester.mealplanner.recipes.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class RecipeMapperTest {
  private final UUID uuid1 = UUID.randomUUID();
  private final UUID uuid2 = UUID.randomUUID();
  private final UUID uuid3 = UUID.randomUUID();
  private final UUID uuid4 = UUID.randomUUID();
  private final String name1 = "recipe 1";
  private final String name2 = "recipe 2";
  private final Ingredient ingredient1 = new Ingredient(uuid3, "ingredient 1", null);
  private final Ingredient ingredient2 = new Ingredient(uuid4, "ingredient 2", null);
  private final IngredientDto ingredientDto1 = new IngredientDto(uuid3, "ingredient 1");
  private final IngredientDto ingredientDto2 = new IngredientDto(uuid4, "ingredient 2");
  private final Recipe recipe1 = new Recipe(uuid1, name1, Set.of(ingredient1, ingredient2));
  private final Recipe recipe2 = new Recipe(uuid2, name2, Set.of(ingredient1));
  private final RecipeDto recipeDto1 =
      new RecipeDto(uuid1, name1, List.of(ingredientDto1, ingredientDto2));
  private final RecipeDto recipeDto2 = new RecipeDto(uuid2, name2, List.of(ingredientDto1));

  private final RecipeMapper recipeMapper = Mappers.getMapper(RecipeMapper.class);

  @Nested
  @DisplayName("Recipe to RecipeDto")
  class RecipeToRecipeDto {
    @Test
    @DisplayName("Single recipe to recipeDto")
    void recipeToDto_singleRecipe_mappedToRecipeDto() {
      assertEquals(recipeDto1, recipeMapper.recipeToDto(recipe1));
    }

    @Test
    @DisplayName("Multiple recipes to recipeDtos")
    void recipeToDto_multipleRecipes_mappedToRecipeDtos() {
      assertEquals(
          List.of(recipeDto1, recipeDto2), recipeMapper.recipeToDto(List.of(recipe1, recipe2)));
    }
  }
}
