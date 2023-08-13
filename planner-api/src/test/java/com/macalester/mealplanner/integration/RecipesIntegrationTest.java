package com.macalester.mealplanner.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.macalester.mealplanner.AuthOpenSecurityConfig;
import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ContextConfiguration(classes = {AuthOpenSecurityConfig.class})
public class RecipesIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeDtoMapper recipeDtoMapper;

    private static final String BASE_URL = "/recipes";

    private static final String ingredient1name = "ingredient a";
    private static final String ingredient2name = "ingredient b";
    private final Ingredient ingredient1 = new Ingredient();
    private final Ingredient ingredient2 = new Ingredient();

    private static final String recipe1name = "recipe a";
    private static final String recipe2name = "recipe b";
    private Recipe recipe1 = new Recipe();
    private Recipe recipe2 = new Recipe();

    @BeforeEach
    void init() {
        ingredient1.setName(ingredient1name);
        ingredient2.setName(ingredient2name);
        ingredientRepository.saveAll(Set.of(ingredient1, ingredient2));
        recipe1.setName(recipe1name);
        recipe2.setName(recipe2name);
    }

    @AfterEach
    void teardown() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Nested
    @DisplayName("Get All Recipes")
    class GetAllRecipesTest {
        @Test
        @DisplayName("Returns recipes in db - JSON")
        void getAllRecipes_returnsAllRecipesInJSON() throws Exception {
            recipe1.setIngredients(Set.of());
            recipe1 = recipeRepository.save(recipe1);
            recipe2.setIngredients(Set.of(ingredient1));
            recipe2 = recipeRepository.save(recipe2);

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String expected = objectMapper.writeValueAsString(Stream.of(recipe1, recipe2).map(recipeDtoMapper).toList());

            assertEquals(expected, responseBody);
        }

        @Test
        @DisplayName("Returns recipes in db - CSV")
        void getAllRecipes_returnsAllRecipesInCsv() throws Exception {
            recipe1.setIngredients(Set.of());
            recipe1 = recipeRepository.save(recipe1);
            recipe2.setIngredients(Set.of(ingredient1));
            recipe2 = recipeRepository.save(recipe2);

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).accept("text/csv"))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String expected = recipe1name + ",\n" + recipe2name + "," + ingredient1name;

            assertEquals(expected, responseBody);
        }
    }

    @Nested
    @DisplayName("Add Recipe")
    class AddRecipeTest {
        @Test
        @DisplayName("Posting valid recipe - returns 200")
        void addNewRecipe_validRecipe_addsRecipe() throws Exception {
            final String newRecipeName = "recipe     a     ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newRecipeName, List.of(ingredient1.getId()));

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipeCreateDto)))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            assertTrue(recipeRepository.existsByName(recipe1name));
            assertAll(() -> assertTrue(responseBody.contains(recipe1name)),
                    () -> assertTrue(responseBody.contains(ingredient1name)));
        }

        @Test
        @DisplayName("Posting recipe with invalid name - returns 400")
        void addNewRecipe_invalidName_returns400() throws Exception {
            final String newRecipeName = "   ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newRecipeName, List.of(ingredient1.getId()));

            mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getErrorMessage();

            assertFalse(recipeRepository.existsByName(newRecipeName));
        }

        @Test
        @DisplayName("Posting valid recipe with a non-unique name returns 400")
        void addNewRecipe_validRecipeWithNonUniqueName_returns400() throws Exception {
            recipeRepository.save(recipe1);
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, List.of(ingredient1.getId()));

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipeCreateDto)))
                            .andExpect(status().isBadRequest())
                            .andReturn()
                            .getResponse()
                            .getErrorMessage();

            assertTrue(responseBody.contains(String.format("Recipe with name %s already exists", recipe1name)));
        }
    }


    @Nested
    @DisplayName("Delete Recipe")
    class DeleteRecipeTest {
        @Test
        @DisplayName("Delete request for recipe in db")
        void deleteRecipe_recipeInDb_deletesRecipe() throws Exception {
            UUID recipe1Id = recipeRepository.save(recipe1).getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + recipe1Id))
                    .andExpect(status().isNoContent());

            assertFalse(recipeRepository.existsById(recipe1Id));
        }

        @Test
        @DisplayName("Delete request for recipe not in db")
        void deleteRecipe_recipeNotInDb_returns200() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());

            assertFalse(recipeRepository.existsById(id));
        }
    }

    @Nested
    @DisplayName("Edit Recipe by Id")
    class EditRecipeByIdTest {
        @Test
        @DisplayName("Edit recipe - valid name")
        void editRecipeById_validName_nameIsUpdatedAndReturns200() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            String newName = "new recipe name";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, List.of());

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipeCreateDto)))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            Optional<Recipe> found = recipeRepository.findById(recipeId);
            assertTrue(found.isPresent());
            assertAll(() -> assertTrue(responseBody.contains(newName)), () -> assertEquals(newName, found.get().getName()));
        }

        @Test
        @DisplayName("Edit recipe - invalid name returns 400")
        void editRecipeById_invalidName_nameIsNotUpdatedAndReturns400() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            String newName = "   ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, List.of());

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Edit recipe - recipe with given id does not exist")
        void editRecipeById_noRecipeWithId_returns400() throws Exception {
            String newName = "recipe c";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, List.of());

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Edit recipe - ingredients in db")
        @Transactional
        void editRecipeById_ingredientsInDb_returns200() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            List<UUID> newIngredients = List.of(ingredient1.getId());
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, newIngredients);

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipeCreateDto)))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            Optional<Recipe> found = recipeRepository.findById(recipeId);
            assertTrue(found.isPresent());
            assertAll(
                    () -> assertEquals(Set.of(ingredient1), found.get().getIngredients()),
                    () -> assertTrue(responseBody.contains(ingredient1name))
            );
        }

        @Test
        @DisplayName("Edit recipe - ingredients not in db")
        @Transactional
        void editRecipeById_ingredientsNotInDb_returns400() throws Exception {
            recipe1.setIngredients(Set.of());
            UUID recipeId = recipeRepository.save(recipe1).getId();
            List<UUID> newIngredients = List.of(UUID.randomUUID());
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, newIngredients);

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isBadRequest());

            Optional<Recipe> found = recipeRepository.findById(recipeId);
            assertTrue(found.isPresent());
            assertEquals(Set.of(), found.get().getIngredients());
        }
    }
}
