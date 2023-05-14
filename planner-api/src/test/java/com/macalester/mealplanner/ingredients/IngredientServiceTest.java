package com.macalester.mealplanner.ingredients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
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
  private final IngredientEditDto ingredientEditDto1 = new IngredientEditDto("name edit");

  @Nested
  @DisplayName("Find ingredient by Id")
  class FindIngredientById {
    @Test
    @DisplayName("Ingredient id in db, returns ingredient")
    void findIngredientById_givenValidId_idInDb_returnsIngredient() {
      doReturn(Optional.of(ingredient1)).when(ingredientRepository).findById(uuid1);

      assertEquals(ingredient1, ingredientService.findById(uuid1));
    }

    @Test
    @DisplayName("Ingredient id not in db, throws NotFoundException")
    void findIngredientById_givenValidId_idNotInDb_throwsNotFoundException() {
      doThrow(NotFoundException.class).when(ingredientRepository).findById(uuid1);

      assertThrows(NotFoundException.class, () -> ingredientService.findById(uuid1));
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

  @Test
  @DisplayName("Delete ingredient by id calls correct repository method")
  void deleteIngredientById_whenMethodCalled_callsRepositoryMethod() {
    ingredientService.deleteById(uuid1);
    verify(ingredientRepository).deleteById(uuid1);
    verifyNoMoreInteractions(ingredientRepository);
  }

  @Nested
  @DisplayName("Edit ingredient")
  class EditIngredient {
    @Test
    @DisplayName("Valid request returns saved ingredient")
    void editIngredient_ingredientInDbAndAllValidFields_ingredientSaved() {
      String newName = "new ingredient name";
      Ingredient ingredient1_edited = new Ingredient(uuid1, newName, null);
      doReturn(Optional.of(ingredient1)).when(ingredientRepository).findById(uuid1);
      doReturn(ingredient1_edited).when(ingredientRepository).save(ingredient1_edited);

      assertEquals(
          ingredient1_edited,
          ingredientService.editIngredientById(uuid1, new IngredientEditDto(newName)));
    }

    @Test
    @DisplayName("Null name is not saved")
    void editIngredient_ingredientInDbAndNameNull_nameNotChanged() {
      doReturn(Optional.of(ingredient1)).when(ingredientRepository).findById(uuid1);
      doReturn(ingredient1).when(ingredientRepository).save(ingredient1);

      assertEquals(
          ingredient1, ingredientService.editIngredientById(uuid1, new IngredientEditDto(null)));
    }

    @Test
    @DisplayName("No ingredient for UUID in database throws NotFoundException")
    void editIngredient_ingredientNotInDb_throwsNotFoundException() {
      doReturn(Optional.empty()).when(ingredientRepository).findById(uuid1);

      assertThrows(
          NotFoundException.class,
          () -> ingredientService.editIngredientById(uuid1, ingredientEditDto1));
    }
  }
}
