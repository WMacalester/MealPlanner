package com.macalester.mealplanner.recipes;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDtoMapper;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

  private final RecipeService recipeService;
  private final RecipeDtoMapper recipeDtoMapper;
  private final RecipeCreateDtoMapper recipeCreateDtoMapper;

  @GetMapping
  public List<Recipe> getAllRecipes() {
    return recipeService.getAllRecipes();
  }

  @PostMapping
  public RecipeDto addRecipe(@Valid @RequestBody RecipeCreateDto recipeCreateDto) {
    try {
      Recipe newRecipe = recipeCreateDtoMapper.apply(recipeCreateDto);
      return recipeDtoMapper.apply(recipeService.addRecipe(newRecipe));
    } catch (UniqueConstraintViolationException | NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
