package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RecipeTest {

  Set<Ingredient> ingredients1 = Set.of(new Ingredient(UUID.randomUUID(), "ingredient 1", null));
  Set<Ingredient> ingredients2 = Set.of(new Ingredient(UUID.randomUUID(), "ingredient 2", null));
  private final Recipe recipe1a = new Recipe(UUID.randomUUID(), "test 1", ingredients1);
  private final Recipe recipe1b = new Recipe(UUID.randomUUID(), "test 1", ingredients2);
  private final Recipe recipe2 = new Recipe(UUID.randomUUID(), "test 2", ingredients1);

  @Nested
  @DisplayName("Equals and HashCode")
  class EqualsAndHashCode {
    @Test
    @DisplayName("Objects are equal")
    void equalsAndHashCode_givenEqualObjects_objectsEqual() {
      assertEquals(recipe1a, recipe1b);
      assertEquals(recipe1a.hashCode(), recipe1b.hashCode());
    }

    @Test
    @DisplayName("Objects are not equal")
    void equalsAndHashCode_givenUnequalObjects_objectsNotEqual() {
      assertNotEquals(recipe1a, recipe2);
      assertNotEquals(recipe1a.hashCode(), recipe2.hashCode());
    }
  }
}
