package com.macalester.mealplanner.ingredients.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class IngredientMapperTest {
  private static final UUID id = UUID.randomUUID();
  private final String name = "test name";
  private final String name2 = "test name 2";

  private final Ingredient ingredient = new Ingredient(id, name, Set.of());
  private final IngredientDto ingredientDto = new IngredientDto(id, name);
  private final IngredientCreateDto ingredientCreateDto = new IngredientCreateDto(name.toUpperCase()+"     ");

  private final Ingredient ingredient2 = new Ingredient(id, name2, Set.of());
  private final IngredientDto ingredientDto2 = new IngredientDto(id, name2);

  private final IngredientCreateDtoMapper ingredientCreateDtoMapper = new IngredientCreateDtoMapper();
  private final IngredientDtoMapper ingredientDtoMapper = new IngredientDtoMapper();

  @Nested
  @DisplayName("Ingredient to IngredientDto")
  class IngredientToIngredientDtoTest {
    @Test
    @DisplayName("Single ingredient to ingredientDto")
    void ingredientToDto_singleIngredient_mappedToIngredientDto() {
      assertEquals(ingredientDto, ingredientDtoMapper.apply(ingredient));
    }

    @Test
    @DisplayName("Multiple ingredients to ingredientDtos")
    void ingredientToDto_multipleIngredients_mappedToIngredientDtos() {
      assertEquals(
          List.of(ingredientDto, ingredientDto2),
          Stream.of(ingredient, ingredient2).map(ingredientDtoMapper).toList());
    }
  }

  @Nested
  @DisplayName("IngredientCreateDto to Ingredient")
  class IngredientCreateDtoToIngredientTest {
    @Test
    void ingredientCreateDTOtoIngredient_givenIngredientCreateDto_returnIngredientWithNameTrimmedAndLowercase() {
      assertEquals(ingredient, ingredientCreateDtoMapper.apply(ingredientCreateDto));
    }
  }
}
