package com.macalester.mealplanner.recipes.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDtoMapper;
import com.macalester.mealplanner.recipes.DietType;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RecipeDtoMapperTest {
  private final RecipeDtoMapper recipeDtoMapper = new RecipeDtoMapper(new IngredientDtoMapper());

  private final UUID uuid1 = UUID.randomUUID();
  private final UUID uuid2 = UUID.randomUUID();
  private final UUID uuid3 = UUID.randomUUID();
  private final UUID uuid4 = UUID.randomUUID();
  private final String name1 = "recipe a";
  private final String name2 = "recipe b";
  private final Ingredient ingredient1 = new Ingredient(uuid3, "ingredient a", null);
  private final Ingredient ingredient2 = new Ingredient(uuid4, "ingredient b", null);
  private final IngredientDto ingredientDto1 = new IngredientDto(uuid3, "ingredient a");
  private final IngredientDto ingredientDto2 = new IngredientDto(uuid4, "ingredient b");
  private final Recipe recipe1 = new Recipe(uuid1, name1, DietType.VEGAN, Set.of(ingredient1, ingredient2));
  private final Recipe recipe_nullIngredients = new Recipe(uuid2, name2, DietType.VEGAN, null);
  private final RecipeDto recipeDto1 =
      new RecipeDto(uuid1, name1, DietType.VEGAN, List.of(ingredientDto1, ingredientDto2));
  private final RecipeDto recipeDto_nullIngredients = new RecipeDto(uuid2, name2, DietType.VEGAN, List.of());

  @Nested
  @DisplayName("Recipe to RecipeDto")
  class RecipeToRecipeDtoTest {
    @Test
    @DisplayName("Recipe to recipeDto, ingredients are not null")
    void recipeToDto_ingredientsNotNull_mappedToRecipeDto() {
      assertEquals(recipeDto1, recipeDtoMapper.apply(recipe1));
    }

    @Test
    @DisplayName("Recipe to recipeDto, ingredients are null")
    void recipeToDto_ingredientsNull_mappedToRecipeDto() {
      assertEquals(recipeDto_nullIngredients, recipeDtoMapper.apply(recipe_nullIngredients));
    }
  }
}
