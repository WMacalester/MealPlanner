package com.macalester.mealplanner.dataexporter;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.recipes.DietType;
import com.macalester.mealplanner.recipes.Recipe;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CsvDataExporterTest {

    private static final CsvDataExporter csvDataExporter = new CsvDataExporter();

    private static final String ingredientName1 = "Ingredient a";
    private static final String ingredientName2 = "Ingredient b";
    private static final String ingredientName3 = "Ingredient c";
    private static final String recipeName1 = "Recipe a";
    private static final String recipeName2 = "Recipe b";
    private static final String recipeName3 = "Recipe c";

    private static final Ingredient ingredient1 = new Ingredient(UUID.randomUUID(), ingredientName1, Set.of());
    private static final Ingredient ingredient2 = new Ingredient(UUID.randomUUID(), ingredientName2, Set.of());
    private static final Ingredient ingredient3 = new Ingredient(UUID.randomUUID(), ingredientName3, Set.of());

    private static final Recipe recipe1 = new Recipe(UUID.randomUUID(), recipeName1, DietType.MEAT, Set.of(ingredient1));
    private static final Recipe recipe2 = new Recipe(UUID.randomUUID(), recipeName2, DietType.VEGAN, Set.of(ingredient1, ingredient2));
    private static final Recipe recipe3 = new Recipe(UUID.randomUUID(), recipeName3, DietType.MEAT, Set.of(ingredient2, ingredient3));


    @Nested
    @DisplayName("Export Ingredients")
    class ExportIngredientsTest {
        @DisplayName("Valid ingredients and filename returns formatted string")
        @Test
        void exportIngredients_emptyFilename_throwsIllegalArgumentException() {
            String expected = String.join("\n", ingredientName1, ingredientName2, ingredientName3);
            assertEquals(expected, csvDataExporter.exportIngredients(List.of(ingredient1, ingredient2, ingredient3)));
        }
    }

    @Nested
    @DisplayName("Export Recipes")
    class ExportRecipesTest {
        @DisplayName("Valid recipes returns formatted string")
        @Test
        void exportIngredients_emptyFilename_throwsIllegalArgumentException() {
            String recipe1Expected = recipeName1 + "," + DietType.MEAT + "," + ingredientName1;
            String recipe2Expected = recipeName2 + "," + DietType.VEGAN + "," + ingredientName1 + "," + ingredientName2;
            String recipe3Expected = recipeName3 + "," + DietType.MEAT + "," + ingredientName2 + "," + ingredientName3;
            String expected = String.join("\n",recipe1Expected,recipe2Expected,recipe3Expected);

            assertEquals(expected, csvDataExporter.exportRecipes(List.of(recipe1,recipe2,recipe3)));
        }
    }
}
