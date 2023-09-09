package com.macalester.mealplanner.dataexporter;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CsvDataExporter implements DataExporter {

    @Override
    public String exportIngredients(Collection<Ingredient> ingredients) {
        return ingredients.stream()
            .map(Ingredient::getName)
            .collect(Collectors.joining("\n"));
    }

    @Override
    public String exportRecipes(Collection<Recipe> recipes) {
        return recipes.stream()
            .map(this::formatRecipe)
            .collect(Collectors.joining("\n"));
    }

    private String formatRecipe(Recipe recipe) {
        return recipe.getName() + "," + recipe.getDietType() + ","
            + recipe.getIngredients().stream().map(Ingredient::getName).sorted().collect(Collectors.joining(","));
    }
}
