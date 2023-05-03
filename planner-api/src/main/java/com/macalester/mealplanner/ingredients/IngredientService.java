package com.macalester.mealplanner.ingredients;

import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
  private final IngredientRepository ingredientRepository;

  public IngredientService(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  public Optional<Ingredient> findById(UUID id) {
    return ingredientRepository.findById(id);
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
}
