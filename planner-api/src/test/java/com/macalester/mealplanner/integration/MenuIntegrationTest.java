package com.macalester.mealplanner.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MenuIntegrationTest extends BaseIntegrationTest {
    @Autowired private RecipeDtoMapper recipeDtoMapper;

    private static final String BASE_URL = "/menu";
    private static final String RECIPE_NUMBER_PARAM = "number";

    private static final String ingredient1Name = "Ingredient 1";
    private static final String ingredient2Name = "Ingredient 2";
    private static final String ingredient3Name = "Ingredient 3";

    private static final String recipe1Name = "Recipe 1";
    private static final String recipe2Name = "Recipe 2";
    private static final String recipe3Name = "Recipe 3";

    private static Ingredient ingredient1 = new Ingredient();
    private static Ingredient ingredient2 = new Ingredient();
    private static Ingredient ingredient3 = new Ingredient();

    private static Recipe recipe1 = new Recipe();
    private static Recipe recipe2 = new Recipe();
    private static Recipe recipe3 = new Recipe();

    @BeforeAll
    static void init(@Autowired IngredientRepository ingredientRepository, @Autowired RecipeRepository recipeRepository){
        ingredient1.setName(ingredient1Name);
        ingredient2.setName(ingredient2Name);
        ingredient3.setName(ingredient3Name);

        ingredient1 = ingredientRepository.save(ingredient1);
        ingredient2 = ingredientRepository.save(ingredient2);
        ingredient3 = ingredientRepository.save(ingredient3);

        recipe1.setName(recipe1Name);
        recipe2.setName(recipe2Name);
        recipe3.setName(recipe3Name);

        recipe1.setIngredients(Set.of());
        recipe2.setIngredients(Set.of(ingredient1));
        recipe3.setIngredients(Set.of(ingredient2, ingredient3));

        recipe1 = recipeRepository.save(recipe1);
        recipe2 = recipeRepository.save(recipe2);
        recipe3 = recipeRepository.save(recipe3);
    }

    @Nested
    @DisplayName("Get Random Unique Recipes")
    class GetRandomUniqueRecipesTest {
        @Test
        @DisplayName("More recipes requested than available - returns 400")
        void getRandomUniqueRecipes_moreRecipesRequestedThanInDb_returns400() throws Exception{
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param(RECIPE_NUMBER_PARAM,"100"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Valid request with enough recipes in db - returns 200 and list of recipes")
        void getRandomUniqueRecipes_validRequestAndEnoughRecipesInDb_returns400() throws Exception {
            String response = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param(RECIPE_NUMBER_PARAM,"3"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String expected = objectMapper.writeValueAsString(
                    Stream.of(recipe1,recipe2,recipe3)
                            .map(recipeDtoMapper)
                            .toList()
            );

            assertEquals(expected,response);
        }

        @Test
        @DisplayName("Request with < 1 number of recipes - returns 400")
        void getRandomUniqueRecipes_requestedLessThan1Recipe_returns400() throws Exception{
           mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param(RECIPE_NUMBER_PARAM,"0"))
                   .andExpect(status().isBadRequest());
        }
    }
}
