package com.macalester.mealplanner.recipes.dto;

import com.macalester.mealplanner.recipes.Recipe;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
  RecipeDto recipeToDto(Recipe recipe);

  List<RecipeDto> recipeToDto(List<Recipe> recipes);
}
