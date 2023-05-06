package com.macalester.mealplanner.ingredients.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredientMapper {
  IngredientMapper INSTANCE = Mappers.getMapper(IngredientMapper.class);

  IngredientDto ingredientToDto(Ingredient ingredient);

  List<IngredientDto> ingredientToDto(List<Ingredient> ingredients);

  Ingredient ingredientCreateDTOtoIngredient(IngredientCreateDto ingredientCreateDTO);
}
