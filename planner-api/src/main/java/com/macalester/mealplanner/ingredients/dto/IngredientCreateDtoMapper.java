package com.macalester.mealplanner.ingredients.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.HashSet;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class IngredientCreateDtoMapper implements Function<IngredientCreateDto, Ingredient> {
    @Override
    public Ingredient apply(IngredientCreateDto ingredientCreateDto) {
        return new Ingredient(null, ingredientCreateDto.name().trim().toLowerCase().replaceAll(" +", " "), new HashSet<>());
    }
}
