package com.macalester.mealplanner.recipes;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDtoMapper;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  public List<RecipeDto> getAllRecipes() {
    return recipeService.getAllRecipes().stream().map(recipeDtoMapper).toList();
  }

  @GetMapping("/{id}")
  public RecipeDto getRecipeById(@PathVariable UUID id) {
    try {
      return recipeDtoMapper.apply(recipeService.findById(id));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
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

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRecipeById(@PathVariable UUID id) {
    recipeService.deleteById(id);
  }

  @PutMapping("/{id}")
  public RecipeDto editRecipeById(
      @PathVariable UUID id, @RequestBody @Valid RecipeCreateDto recipeCreateDto) {
    try {
      return recipeDtoMapper.apply(
          recipeService.editRecipeById(id, recipeCreateDtoMapper.apply(recipeCreateDto)));
    } catch (UniqueConstraintViolationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (NotFoundException e) {
      if (e.getMessage().toLowerCase().contains("recipe")) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
