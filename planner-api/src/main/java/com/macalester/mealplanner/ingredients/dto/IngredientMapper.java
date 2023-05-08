package com.macalester.mealplanner.ingredients.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
  IngredientDto ingredientToDto(Ingredient ingredient);

  List<IngredientDto> ingredientToDto(List<Ingredient> ingredients);

  Ingredient ingredientCreateDTOtoIngredient(IngredientCreateDto ingredientCreateDTO);

}
