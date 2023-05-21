package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
  private static final String name1 = "recipe 1";
  private static final String name2 = "recipe 2";

  private final Recipe recipe1 = new Recipe(uuid1, name1, Set.of());
  private final Recipe recipe2 = new Recipe(uuid2, name2, Set.of());
  private final Ingredient ingredient1 =
      new Ingredient(UUID.randomUUID(), "ingredient 1", Set.of());

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

  @Test
  @DisplayName("Delete recipe by id calls correct repository method")
  void deleteRecipeById_whenMethodCalled_callsRepositoryMethod() {
    recipeService.deleteById(uuid1);
    verify(recipeRepository).deleteById(uuid1);
    verifyNoMoreInteractions(recipeRepository);
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

  @Nested
  @DisplayName("Edit recipe by Id")
  class EditRecipeById {
    private final Recipe recipe_NullId1 = new Recipe(null, name1, Set.of());
    private final Recipe recipe_NullId2 = new Recipe(null, name2, Set.of());

    @Test
    @DisplayName("Recipe with given id not in database throws NotFoundException")
    void editRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() {
      doReturn(Optional.empty()).when(recipeRepository).findById(uuid1);
      assertThrows(NotFoundException.class, () -> recipeService.editRecipeById(uuid1, recipe1));
    }

    @Test
    @DisplayName("All fields updated and returns recipe with correct id")
    void editRecipeById_validRecipeObjectAndIdInDb_savesAndReturnsUpdatedRecipe() {
      Recipe recipe1_allUpdated = new Recipe(uuid1, name2, Set.of(ingredient1));
      doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
      doReturn(recipe1_allUpdated).when(recipeRepository).save(recipe1_allUpdated);

      assertEquals(recipe1_allUpdated, recipeService.editRecipeById(uuid1, recipe_NullId2));
    }

    @Nested
    @DisplayName("Name changes")
    class EditRecipeById_NameChanges {
      @Test
      @DisplayName("Name is not changed, saves and returns updated recipe")
      void editRecipeById_nameNotChanged_savesAndReturnsUpdatedRecipe() {
        Recipe recipe_allUpdated = new Recipe(uuid1, name1, Set.of(ingredient1));
        doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
        doReturn(recipe_allUpdated).when(recipeRepository).save(recipe_allUpdated);

        assertEquals(recipe_allUpdated, recipeService.editRecipeById(uuid1, recipe_NullId1));
      }

      @Test
      @DisplayName("Name is changed and is not unique, throws UniqueConstraintViolationException")
      void editRecipeById_nameIsChangedAndNotUnique_throwsUniqueConstraintViolationException() {
        doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
        doReturn(true).when(recipeRepository).existsByName(name2);

        assertThrows(
            UniqueConstraintViolationException.class,
            () -> recipeService.editRecipeById(uuid1, recipe_NullId2));
      }

      @Test
      @DisplayName("Name is changed and is unique, returns updated recipe")
      void editRecipeById_nameIsChangedAndUnique_savesAndReturnsUpdatedRecipe() {
        Recipe recipe_allUpdated = new Recipe(uuid1, name2, Set.of(ingredient1));
        doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
        doReturn(false).when(recipeRepository).existsByName(name2);
        doReturn(recipe_allUpdated).when(recipeRepository).save(recipe_allUpdated);

        assertEquals(recipe_allUpdated, recipeService.editRecipeById(uuid1, recipe_NullId2));
      }
    }
  }
}
