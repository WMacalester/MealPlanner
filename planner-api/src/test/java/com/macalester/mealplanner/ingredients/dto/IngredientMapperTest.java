package com.macalester.mealplanner.ingredients.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class IngredientMapperTest {
  private static final UUID id = UUID.randomUUID();
  private final String name = "test name";
  private final String name2 = "test name 2";

  // todo update with recipes when finishing recipe mapping
  private final Ingredient ingredient = new Ingredient(id, name, null);
  private final IngredientDto ingredientDto = new IngredientDto(id, name);
  private final IngredientCreateDto ingredientCreateDto = new IngredientCreateDto(name);

  private final Ingredient ingredient2 = new Ingredient(id, name2, null);
  private final IngredientDto ingredientDto2 = new IngredientDto(id, name2);

  private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

  @Nested
  @DisplayName("Ingredient to IngredientDto")
  class IngredientToIngredientDto {
    @Test
    @DisplayName("Single ingredient to ingredientDto")
    void ingredientToDto_singleIngredient_mappedToIngredientDto() {
      assertEquals(ingredientDto, mapper.ingredientToDto(ingredient));
    }

    @Test
    @DisplayName("Multiple ingredients to ingredientDtos")
    void ingredientToDto_multipleIngredients_mappedToIngredientDtos() {
      assertEquals(
          List.of(ingredientDto, ingredientDto2),
          mapper.ingredientToDto(List.of(ingredient, ingredient2)));
    }
  }

  @Nested
  @DisplayName("IngredientCreateDto to Ingredient")
  class IngredientCreateDtoToIngredient {
    @Test
    void ingredientCreateDTOtoIngredient_givenIngredientCreateDto_returnIngredient() {
      assertEquals(ingredient, mapper.ingredientCreateDTOtoIngredient(ingredientCreateDto));
    }
  }
}
