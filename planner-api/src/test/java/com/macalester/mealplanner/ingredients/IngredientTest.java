package com.macalester.mealplanner.ingredients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IngredientTest {

  private final Ingredient ingredient1a = new Ingredient(UUID.randomUUID(), "test 1", null);
  private final Ingredient ingredient1b = new Ingredient(UUID.randomUUID(), "test 1", null);
  private final Ingredient ingredient2 = new Ingredient(UUID.randomUUID(), "test 2", null);

  @Nested
  @DisplayName("Equals and HashCode")
  class EqualsAndHashCode {
    @Test
    @DisplayName("Objects are equal")
    void equalsAndHashCode_givenEqualObjects_objectsEqual() {
      assertEquals(ingredient1a, ingredient1b);
      assertEquals(ingredient1a.hashCode(), ingredient1b.hashCode());
    }

    @Test
    @DisplayName("Objects are not equal")
    void equalsAndHashCode_givenUnequalObjects_objectsNotEqual() {
      assertNotEquals(ingredient1a, ingredient2);
      assertNotEquals(ingredient1a.hashCode(), ingredient2.hashCode());
    }
  }
}
