package com.macalester.mealplanner.dataloader;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.Set;

public interface DataLoader {
    Set<Ingredient> loadIngredients(String file);
    Set<Recipe> loadRecipes(String file);
}
