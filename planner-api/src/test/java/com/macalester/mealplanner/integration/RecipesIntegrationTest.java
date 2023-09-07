package com.macalester.mealplanner.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.DietType;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.transaction.Transactional;
import java.util.HashSet;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    private Recipe recipe1 = new Recipe(UUID.randomUUID(), recipe1name, DietType.MEAT, new HashSet<>());
    private Recipe recipe2 = new Recipe(UUID.randomUUID(), recipe2name, DietType.MEAT, new HashSet<>());

    @BeforeEach
    void init() {
        ingredient1.setName(ingredient1name);
        ingredient2.setName(ingredient2name);
        ingredientRepository.saveAll(Set.of(ingredient1, ingredient2));
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
        @WithMockUser(roles = "USER")
        void getAllRecipes_returnsAllRecipesInJSON() throws Exception {
            recipe1 = recipeRepository.save(recipe1);
            recipe2 = recipeRepository.save(recipe2);
            List<Recipe> recipes = List.of(recipe1, recipe2);

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String expected = objectMapper.writeValueAsString(recipes.stream().map(recipeDtoMapper).toList());

            assertEquals(expected, responseBody);
        }

        @Nested
        @DisplayName("Filter")
        class FilterTest {
            @Nested
            @DisplayName("Name")
            class NameFilterTest {
                @Test
                @DisplayName("Valid nameFilter returns list of recipes which contain nameFilter in name or in an ingredient name")
                @WithMockUser(roles = "USER")
                void getAllRecipesWithValidFilter_returnsFilteredRecipesInJSON() throws Exception {
                    recipe1 = recipeRepository.save(recipe1);
                    recipeRepository.save(recipe2);
                    Recipe recipe3 = recipeRepository.save(new Recipe(null, "recipe with ingredient through filter", DietType.VEGAN, Set.of(ingredient1)));
                    recipeRepository.save(new Recipe(null, "recipe without ingredient through filter", DietType.VEGAN, Set.of(ingredient2)));

                    String responseBody =
                            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("recipeName", "a"))
                                    .andExpect(status().isOk())
                                    .andReturn()
                                    .getResponse()
                                    .getContentAsString();

                    String expected = objectMapper.writeValueAsString(Stream.of(recipe1, recipe3).map(recipeDtoMapper).toList());

                    assertEquals(expected, responseBody);
                }

                @Test
                @DisplayName("Invalid nameFilter returns 400")
                @WithMockUser(roles = "USER")
                void getAllRecipesWithInvalidFilter_returns400() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("recipeName", " ^ "))
                            .andExpect(status().isBadRequest());
                }
            }

            @Nested
            @DisplayName("Diet Type")
            class DietTypeTest {
                @Test
                @DisplayName("Valid dietTypeFilter returns list of recipes which contain dietType")
                @WithMockUser(roles = "USER")
                void getAllRecipesWithValidDietTypeFilter_returnsFilteredRecipesInJSON() throws Exception {
                    recipe1 = recipeRepository.save(recipe1);
                    recipeRepository.save(recipe2);
                    Recipe recipe3 = recipeRepository.save(new Recipe(null, "recipe with dietType through filter", DietType.VEGAN, Set.of(ingredient1)));

                    String responseBody =
                            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("dietType", String.valueOf(DietType.VEGAN)))
                                    .andExpect(status().isOk())
                                    .andReturn()
                                    .getResponse()
                                    .getContentAsString();

                    String expected = objectMapper.writeValueAsString(List.of(recipeDtoMapper.apply(recipe3)));

                    assertEquals(expected, responseBody);
                }

                @Test
                @DisplayName("Invalid dietTypeFilter returns 400")
                @WithMockUser(roles = "USER")
                void getAllRecipesWithInvalidDietTypeFilter_returns400() throws Exception {
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("dietType", "INVALID"))
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }

    @Nested
    @DisplayName("Get All Recipes CSV")
    class GetAllRecipesCSVTest {
        @Test
        @DisplayName("Returns recipes in db - CSV")
        @WithMockUser(roles = "ADMIN")
        void getAllRecipes_userIsAdmin_returnsAllRecipesInCsv() throws Exception {
            recipe2.setIngredients(Set.of(ingredient1));
            recipeRepository.saveAll(List.of(recipe1, recipe2));

            String responseBody =
                    mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).accept("text/csv"))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String expected = recipe1name + ",\n" + recipe2name + "," + ingredient1name;

            assertEquals(expected, responseBody);
        }

        @Test
        @DisplayName("User with role User is forbidden from downloading recipes in csv format")
        @WithMockUser(roles = "USER")
        void getAllRecipes_userIsRoleUser_returns403() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).accept("text/csv"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Get recipe by id")
    class GetRecipeByIdTest {
        @Test
        @DisplayName("Return recipe with given id that is in database")
        @WithMockUser(roles = "USER")
        void getRecipeById_recipeWithIdInDatabase_returnsRecipe() throws Exception {
            Recipe savedRecipe = recipeRepository.save(recipe1);
            RecipeDto expected = recipeDtoMapper.apply(savedRecipe);
            mockMvc
                    .perform(MockMvcRequestBuilders.get("/recipes/" + savedRecipe.getId()))
                    .andExpect(status().isOk())
                    .andExpect(
                            MockMvcResultMatchers.content()
                                    .string(equalTo(objectMapper.writeValueAsString(expected))));
        }

        @Test
        @DisplayName("Throw NotFoundException if no recipe with given id is in database")
        @WithMockUser(roles = "USER")
        void getRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() throws Exception {
            mockMvc
                    .perform(MockMvcRequestBuilders.get("/recipes/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Add Recipe")
    class AddRecipeTest {
        @Test
        @DisplayName("Posting valid recipe - returns 200")
        @WithMockUser(roles = "ADMIN")
        void addNewRecipe_validRecipe_addsRecipe() throws Exception {
            final String newRecipeName = "recipe     a     ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newRecipeName, DietType.MEAT, List.of(ingredient1.getId()));

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
        @WithMockUser(roles = "ADMIN")
        void addNewRecipe_invalidName_returns400() throws Exception {
            final String newRecipeName = "   ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newRecipeName, DietType.MEAT, List.of(ingredient1.getId()));

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
        @WithMockUser(roles = "ADMIN")
        void addNewRecipe_validRecipeWithNonUniqueName_returns400() throws Exception {
            recipeRepository.save(recipe1);
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, DietType.MEAT, List.of(ingredient1.getId()));

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

        @Test
        @DisplayName("Invalid recipe, name is null")
        @WithMockUser(roles = "ADMIN")
        void addRecipeNoIngredients_invalidRecipeNullName_returns400() throws Exception {
            Recipe recipe_nullName = new Recipe(null, null, null, null);
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/recipes")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipe_nullName)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Invalid recipe, name is blank")
        @WithMockUser(roles = "ADMIN")
        void addRecipeNoIngredients_invalidRecipeBlankName_returns400() throws Exception {
            Recipe recipe_blankName = new Recipe(null, "   ", null, null);
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/recipes")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipe_blankName)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("User has role user, returns 403")
        @WithMockUser(roles = "USER")
        void addRecipeNoIngredients_userHasRoleUser_returns403() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/recipes")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(recipe1)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Delete Recipe")
    class DeleteRecipeTest {
        @Test
        @DisplayName("Delete request for recipe in db")
        @WithMockUser(roles = "ADMIN")
        void deleteRecipe_recipeInDb_deletesRecipe() throws Exception {
            UUID recipe1Id = recipeRepository.save(recipe1).getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + recipe1Id))
                    .andExpect(status().isNoContent());

            assertFalse(recipeRepository.existsById(recipe1Id));
        }

        @Test
        @DisplayName("Delete request for recipe not in db")
        @WithMockUser(roles = "ADMIN")
        void deleteRecipe_recipeNotInDb_returns200() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());

            assertFalse(recipeRepository.existsById(id));
        }

        @Test
        @DisplayName("User has role user, returns 403")
        @WithMockUser(roles = "USER")
        void deleteRecipe_userHasRoleUser_returns403() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + UUID.randomUUID()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Edit Recipe by Id")
    class EditRecipeByIdTest {
        @Test
        @DisplayName("Edit recipe - valid name")
        @WithMockUser(roles = "ADMIN")
        void editRecipeById_validName_nameIsUpdatedAndReturns200() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            String newName = "new recipe name";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, DietType.VEGAN, List.of());

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
            assertAll(() -> assertTrue(responseBody.contains(newName)), () -> assertEquals(newName, found.get().getName()), () -> assertTrue(responseBody.contains("VEGAN")));
        }

        @Test
        @DisplayName("Edit recipe - invalid name returns 400")
        @WithMockUser(roles = "ADMIN")
        void editRecipeById_invalidName_nameIsNotUpdatedAndReturns400() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            String newName = "   ";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, DietType.VEGAN, List.of());

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Edit recipe - recipe with given id does not exist")
        @WithMockUser(roles = "ADMIN")
        void editRecipeById_noRecipeWithId_returns400() throws Exception {
            String newName = "recipe c";
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(newName, DietType.VEGAN, List.of());

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Edit recipe - ingredients in db")
        @WithMockUser(roles = "ADMIN")
        @Transactional
        void editRecipeById_ingredientsInDb_returns200() throws Exception {
            UUID recipeId = recipeRepository.save(recipe1).getId();
            List<UUID> newIngredients = List.of(ingredient1.getId());
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, DietType.VEGAN, newIngredients);

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
        @WithMockUser(roles = "ADMIN")
        @Transactional
        void editRecipeById_ingredientsNotInDb_returns400() throws Exception {
            recipe1.setIngredients(Set.of());
            UUID recipeId = recipeRepository.save(recipe1).getId();
            List<UUID> newIngredients = List.of(UUID.randomUUID());
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, DietType.VEGAN, newIngredients);

            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + recipeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isBadRequest());

            Optional<Recipe> found = recipeRepository.findById(recipeId);
            assertTrue(found.isPresent());
            assertEquals(Set.of(), found.get().getIngredients());
        }

        @Test
        @DisplayName("User has role user, returns 403")
        @WithMockUser(roles = "USER")
        void editRecipeById_userHasRoleUser_returns403() throws Exception {
            RecipeCreateDto recipeCreateDto = new RecipeCreateDto(recipe1name, DietType.VEGAN, List.of());
            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(recipeCreateDto)))
                    .andExpect(status().isForbidden());
        }
    }
}
