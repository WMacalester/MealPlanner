package com.macalester.mealplanner.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.macalester.mealplanner.BasePostgresContainer;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.RecipeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest
@ContextConfiguration(initializers = OnStartLoaderTest.OnStartLoaderInitialiser.class)
class OnStartLoaderTest extends BasePostgresContainer {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private DataLoader dataLoader;

    @AfterAll
    static void teardown(@Autowired IngredientRepository ingredientRepository, @Autowired RecipeRepository recipeRepository) {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    void csvDataLoaderIsLoaded() {
        assertEquals(CsvDataLoader.class, dataLoader.getClass());
    }

    @Test
    void onStart_ingredientsAndRecipesLoadedAndSaved() {
        assertAll(
                () -> assertEquals(5, ingredientRepository.findAll().size()),
                () -> assertEquals(5, recipeRepository.findAll().size())
        );
    }

    static class OnStartLoaderInitialiser implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "data.loader.filetype=csv");
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "data.loader.ingredients=dataloader/csv/ingredients.csv");
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "data.loader.recipes=dataloader/csv/recipes.csv");
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "data.loader.onStart=true");

        }
    }

}
