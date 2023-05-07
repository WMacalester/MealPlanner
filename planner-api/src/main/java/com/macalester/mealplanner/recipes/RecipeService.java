package com.macalester.mealplanner.recipes;

import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

  private final RecipeRepository recipeRepository;

  @Autowired
  public RecipeService(RecipeRepository recipeRepository) {
    this.recipeRepository = recipeRepository;
  }

  public List<Recipe> getAllRecipes() {
    return recipeRepository.findAll();
  }

  public Recipe addRecipe(Recipe recipe) {
    if (recipeRepository.existsByName(recipe.getName())) {
      throw new UniqueConstraintViolationException(
          String.format("Recipe with name %s already exists", recipe.getName()));
    }
    return recipeRepository.save(recipe);
  }
}
