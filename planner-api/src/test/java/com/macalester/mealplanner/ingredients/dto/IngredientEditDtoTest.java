package com.macalester.mealplanner.ingredients.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IngredientEditDtoTest {

  private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = validatorFactory.getValidator();

  @Nested
  @DisplayName("Name")
  class IngredientEditDto_Name {

    @Test
    @DisplayName("Name can be have non-whitespace characters")
    void IngredientEditDto_givenNameHasNonWhiteSpaceCharacters_createObject() {
      String testName = "test name";
      IngredientEditDto ingredientEditDto = new IngredientEditDto(testName);
      Set<ConstraintViolation<IngredientEditDto>> violations =
          validator.validate(ingredientEditDto);
      assertEquals(testName, ingredientEditDto.name());
    }

    @Test
    @DisplayName("Name can be null")
    void IngredientEditDto_givenNameisNull_createObject() {
      IngredientEditDto ingredientEditDto_nullName = new IngredientEditDto(null);
      Set<ConstraintViolation<IngredientEditDto>> violations =
          validator.validate(ingredientEditDto_nullName);
      assertNull(ingredientEditDto_nullName.name());
    }

    @Test
    @DisplayName("Name can not be blank")
    void IngredientEditDto_givenNameIsBlank_throwsConstraintViolationException() {
      Set<ConstraintViolation<IngredientEditDto>> violations =
          validator.validate(new IngredientEditDto("   "));
      assertEquals(1, violations.size());

      violations = validator.validate(new IngredientEditDto(""));
      assertEquals(1, violations.size());

      violations = validator.validate(new IngredientEditDto("\n "));
      assertEquals(1, violations.size());
    }
  }
}
