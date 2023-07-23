package com.macalester.mealplanner.dataloader;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value="data.loader.onStart")
@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OnStartLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final DataLoader dataLoader;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    @Value("${data.loader.ingredients}")
    private String ingredientsFile;
    @Value("${data.loader.recipes}")
    private String recipesFile;

    private boolean initialised = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialised) {
            return;
        }

        log.info("Loading data");
        loadIngredients();

        if (recipesFile != null && !recipeRepository.findAll().iterator().hasNext()) {
            Set<Recipe> recipes = dataLoader.loadRecipes(recipesFile);
            recipeRepository.saveAll(recipes);
            log.info(String.format("Loaded %s recipes", recipes.size()));
        }

        initialised = true;
    }

    private void loadIngredients(){
        if (ingredientsFile != null && !ingredientRepository.findAll().iterator().hasNext()) {
            Set<Ingredient> ingredients = dataLoader.loadIngredients(ingredientsFile);
            ingredientRepository.saveAll(ingredients);
            log.info(String.format("Loaded %s ingredients", ingredients.size()));
        }
    }
}
