package com.macalester.mealplanner.dataloader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.Recipe;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvDataLoaderTest {
    @Mock
    IngredientRepository ingredientRepository;
    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @InjectMocks
    private CsvDataLoader dataLoader;

    private static final Ingredient tomato = new Ingredient(null, "tomato", null);
    private static final Ingredient carrots = new Ingredient(null, "carrots", null);
    private static final Ingredient peas = new Ingredient(null, "peas", null);
    private static final Ingredient pasta = new Ingredient(null, "pasta", null);
    private static final Ingredient potato = new Ingredient(null, "potato", null);
    @Test
    void loadIngredients_onlyValidIngredientsLoadedAndAreProperlyFormatted(){
        Set<Ingredient> ingredients = dataLoader.loadIngredients("dataloader/csv/ingredients.csv");

        Ingredient beefMince = new Ingredient(null, "beef mince", null);

        assertEquals(Set.of(potato,peas,beefMince,pasta,tomato), ingredients);
    }

    @Test
    void loadRecipe_onlyValidRecipesLoadedAndAreProperlyFormatted_notFoundIngredientsExcluded(){
        Recipe recipe1 = new Recipe();
        recipe1.setName("vegetables");
        recipe1.setIngredients(Set.of(tomato, carrots,peas));

        Recipe recipe2 = new Recipe();
        recipe2.setName("carbohydrates");
        recipe2.setIngredients(Set.of(pasta, potato));

        Recipe recipe3 = new Recipe();
        recipe3.setName("empty without comma");
        recipe3.setIngredients(Set.of());

        Recipe recipe4 = new Recipe();
        recipe4.setName("empty with comma");
        recipe4.setIngredients(Set.of());

        Recipe recipe5 = new Recipe();
        recipe5.setName("empty with lots of commas");
        recipe5.setIngredients(Set.of());

        doReturn(Optional.of(tomato)).when(ingredientRepository).findByName("tomato");
        doReturn(Optional.of(carrots)).when(ingredientRepository).findByName("carrots");
        doReturn(Optional.of(peas)).when(ingredientRepository).findByName("peas");
        doReturn(Optional.of(pasta)).when(ingredientRepository).findByName("pasta");
        doReturn(Optional.of(potato)).when(ingredientRepository).findByName("potato");
        doReturn(Optional.empty()).when(ingredientRepository).findByName("onion");

        Set<Recipe> recipes = dataLoader.loadRecipes("dataloader/csv/recipes.csv");

        assertEquals(Set.of(recipe1,recipe2,recipe3,recipe4, recipe5), recipes);
    }
}
