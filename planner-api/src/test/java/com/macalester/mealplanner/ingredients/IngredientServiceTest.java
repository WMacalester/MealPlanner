package com.macalester.mealplanner.ingredients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
class IngredientServiceTest {

  @Mock private IngredientRepository ingredientRepository;
  @InjectMocks private IngredientService ingredientService;

  private static final UUID uuid1 = UUID.fromString("c990ae5e-cd1f-4ce3-94db-e211e4d67b53");
  private static final UUID uuid2 = UUID.fromString("b67b2920-0ace-4714-9614-d429551c022c");

  private final Ingredient ingredient1 = new Ingredient(uuid1, "test ingredient 1", null);
  private final Ingredient ingredient2 = new Ingredient(uuid2, "test ingredient 1", null);

  @Nested
  @DisplayName("Find ingredient by Id")
  class FindIngredientById {
    @Test
    void findIngredientById_givenValidId_idInDb_returnsIngredient() {
      doReturn(Optional.of(ingredient1)).when(ingredientRepository).findById(uuid1);

      assertEquals(Optional.of(ingredient1), ingredientService.findById(uuid1));
    }
  }

  @Nested
  @DisplayName("Find all ingredients")
  class FindAllIngredients {
    @Test
    void findAllIngredients_givenIngredientsInDb_returnListOfIngredients() {
      doReturn(List.of(ingredient1, ingredient2)).when(ingredientRepository).findAll();

      assertEquals((List.of(ingredient1, ingredient2)), ingredientService.findAll());
    }
  }

  @Nested
  @DisplayName("Save Ingredient")
  class SaveIngredient {
    @Test
    @DisplayName("Saves valid ingredient that has unique name")
    void saveIngredient_givenIngredientAndUniqueName_savesIngredient() {
      doReturn(false).when(ingredientRepository).existsByName(ingredient1.getName());
      doReturn(ingredient1).when(ingredientRepository).save(ingredient1);

      assertEquals(ingredient1, ingredientService.save(ingredient1));
    }

    @Test
    @DisplayName(
        "Valid ingredient that does not have a unique name throws UniqueConstrainViolationException")
    void
        saveIngredient_givenIngredientAndNameAlreadyExists_throwsUniqueConstraintViolationException() {
      doReturn(true).when(ingredientRepository).existsByName(ingredient1.getName());

      assertThrows(
          UniqueConstraintViolationException.class, () -> ingredientService.save(ingredient1));
      verifyNoMoreInteractions(ingredientRepository);
    }
  }
}
