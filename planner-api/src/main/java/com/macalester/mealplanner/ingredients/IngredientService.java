package com.macalester.mealplanner.ingredients;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
  private final IngredientRepository ingredientRepository;

  public IngredientService(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  public Ingredient findById(UUID id) {
    return ingredientRepository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Ingredient with id %s was not found", id)));
  }

  public List<Ingredient> findAll() {
    return ingredientRepository.findAll();
  }

  public Ingredient save(Ingredient ingredient) {
    if (ingredientRepository.existsByName(ingredient.getName())) {
      throw new UniqueConstraintViolationException(
          String.format("Ingredient with name %s already exists", ingredient.getName()));
    }
    return ingredientRepository.save(ingredient);
  }

  public Ingredient editIngredientById(UUID id, IngredientEditDto editIngredientDto) {
    Ingredient ingredient = findById(id);

    if (editIngredientDto.name() != null) {
      ingredient.setName(editIngredientDto.name());
    }

    return save(ingredient);
  }

  public void deleteById(UUID id) {
    ingredientRepository.deleteById(id);
  }
}
