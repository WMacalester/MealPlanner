package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

  @InjectMocks private RecipeService recipeService;
  @Mock private RecipeRepository recipeRepository;

  private final Recipe recipe1 = new Recipe(UUID.randomUUID(), "recipe 1");
  private final Recipe recipe2 = new Recipe(UUID.randomUUID(), "recipe 2");

  private final List<Recipe> recipes = List.of(recipe1, recipe2);

  @Test
  @DisplayName("Gets all recipes")
  void getAllRecipes() {
    doReturn(recipes).when(recipeRepository).findAll();
    assertEquals(recipes, recipeService.getAllRecipes());
  }
}
