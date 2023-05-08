package com.macalester.mealplanner.recipes.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientService;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecipeCreateDtoMapperTest {
  @Mock private IngredientService ingredientService;
  @InjectMocks private RecipeCreateDtoMapper recipeCreateDtoMapper;

  private final UUID uuid1 = UUID.randomUUID();
  private final UUID uuid2 = UUID.randomUUID();
  private final UUID uuid3 = UUID.randomUUID();
  private final String name1 = "recipe 1";
  private final Ingredient ingredient1 = new Ingredient(uuid2, "ingredient 1", null);
  private final Ingredient ingredient2 = new Ingredient(uuid3, "ingredient 2", null);
  private final Recipe recipe1 = new Recipe(uuid1, name1, List.of(ingredient1, ingredient2));
  private final Recipe recipe2 = new Recipe(uuid1, name1, List.of());
  private final RecipeCreateDto recipeCreateDto1 =
      new RecipeCreateDto(name1, List.of(uuid2, uuid3));
  private final RecipeCreateDto recipeCreateDto2 = new RecipeCreateDto(name1, List.of());
  private final RecipeCreateDto recipeCreateDto2_null = new RecipeCreateDto(name1, null);

  @Nested
  @DisplayName("Recipe create dto to Recipe")
  class RecipeCreateDtoToRecipe {
    @Test
    @DisplayName("Valid mapping, ingredients in database")
    void recipeCreateDtoToRecipe_givenValidObjectsAndIngredientsInDatabase_returnsMappedObject() {
      doReturn(ingredient1).when(ingredientService).findById(uuid2);
      doReturn(ingredient2).when(ingredientService).findById(uuid3);

      assertEquals(recipe1, recipeCreateDtoMapper.apply(recipeCreateDto1));
    }

    @Test
    @DisplayName("Valid mapping, ingredients not in database")
    void
        recipeCreateDtoToRecipe_givenValidObjectsAndIngredientsNotInDatabase_throwsNotFoundException() {
      doThrow(NotFoundException.class).when(ingredientService).findById(uuid2);

      assertThrows(NotFoundException.class, () -> recipeCreateDtoMapper.apply(recipeCreateDto1));
    }

    @Test
    @DisplayName("Valid mapping, no ingredients maps to empty list")
    void recipeCreateDtoToRecipe_givenValidObjectsAndNoIngredients_mapsToEmptyListIngredients() {
      assertEquals(recipe2, recipeCreateDtoMapper.apply(recipeCreateDto2));
    }

    @Test
    @DisplayName("Valid mapping, null ingredients maps to empty list")
    void recipeCreateDtoToRecipe_givenValidObjectsAndNullIngredients_mapsToEmptyListIngredients() {
      assertEquals(recipe2, recipeCreateDtoMapper.apply(recipeCreateDto2_null));
    }
  }
}
