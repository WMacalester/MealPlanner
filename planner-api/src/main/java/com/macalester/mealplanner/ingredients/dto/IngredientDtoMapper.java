package com.macalester.mealplanner.ingredients.dto;

import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class IngredientDtoMapper implements Function<Ingredient, IngredientDto> {
    @Override
    public IngredientDto apply(Ingredient ingredient) {
        return new IngredientDto(ingredient.getId(), ingredient.getName());
    }
}
