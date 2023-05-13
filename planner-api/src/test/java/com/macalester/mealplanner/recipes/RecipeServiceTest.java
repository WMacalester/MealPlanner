package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

  @InjectMocks private RecipeService recipeService;
  @Mock private RecipeRepository recipeRepository;

  private static final UUID uuid1 = UUID.randomUUID();
  private static final UUID uuid2 = UUID.randomUUID();

  private final Recipe recipe1 = new Recipe(uuid1, "recipe 1", null);
  private final Recipe recipe2 = new Recipe(uuid2, "recipe 2", null);

  private final List<Recipe> recipes = List.of(recipe1, recipe2);

  @Test
  @DisplayName("Gets all recipes")
  void getAllRecipes() {
    doReturn(recipes).when(recipeRepository).findAll();
    assertEquals(recipes, recipeService.getAllRecipes());
  }

  @Nested
  @DisplayName("Get recipe by id")
  class GetRecipeById {
    @Test
    @DisplayName("Return recipe with given id that is in database")
    void getRecipeById_recipeWithIdInDatabase_returnsRecipe() {
      doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);

      assertEquals(recipe1, recipeService.findById(uuid1));
    }

    @Test
    @DisplayName("Throw NotFoundException if no recipe with given id is in database")
    void getRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() {
      doReturn(Optional.empty()).when(recipeRepository).findById(uuid1);

      assertThrows(NotFoundException.class, () -> recipeService.findById(uuid1));
    }
  }

  @Nested
  @DisplayName("Add recipe")
  class AddRecipe {
    @Test
    @DisplayName("Saves valid recipe that has unique name")
    void addRecipe_givenRecipeAndUniqueName_savesIngredient() {
      doReturn(false).when(recipeRepository).existsByName(recipe1.getName());
      doReturn(recipe1).when(recipeRepository).save(recipe1);

      assertEquals(recipe1, recipeService.addRecipe(recipe1));
    }

    @Test
    @DisplayName(
        "Valid recipe that does not have a unique name throws UniqueConstrainViolationException")
    void saveIngredient_givenRecipeAndNameAlreadyExists_throwsUniqueConstraintViolationException() {
      doReturn(true).when(recipeRepository).existsByName(recipe1.getName());

      assertThrows(
          UniqueConstraintViolationException.class, () -> recipeService.addRecipe(recipe1));
      verifyNoMoreInteractions(recipeRepository);
    }
  }
}
