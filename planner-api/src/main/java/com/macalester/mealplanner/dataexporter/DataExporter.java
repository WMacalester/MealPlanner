package com.macalester.mealplanner.dataexporter;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.Collection;

public interface DataExporter {
    String exportIngredients(Collection<Ingredient> ingredients);
    String exportRecipes(Collection<Recipe> recipes);
}
