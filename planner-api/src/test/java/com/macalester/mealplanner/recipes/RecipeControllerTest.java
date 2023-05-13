package com.macalester.mealplanner.recipes;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientService;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDtoMapper;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

  @Autowired private MockMvc mockMvc;
  @SpyBean private RecipeDtoMapper recipeDtoMapper;
  @SpyBean private RecipeCreateDtoMapper recipeCreateDtoMapper;
  @MockBean private RecipeService recipeService;
  @MockBean private IngredientService ingredientService;
  @Autowired private ObjectMapper objectMapper;

  private static final String name1 = "recipe 1";
  private static final String name2 = "recipe 2";
  private static final UUID uuid1 = UUID.randomUUID();
  private static final UUID uuid2 = UUID.randomUUID();
  private static final UUID uuid3 = UUID.randomUUID();

  private final Ingredient ingredient1 = new Ingredient(uuid3, "ingredient 1", Set.of());
  private final IngredientDto ingredientDto1 = new IngredientDto(uuid3, "ingredient 1");
  private final Recipe recipe1 = new Recipe(uuid1, name1, null);
  private final RecipeDto recipeDto1 = new RecipeDto(uuid1, name1, List.of());
  private final RecipeCreateDto recipeCreateDto1 = new RecipeCreateDto(name1, List.of());
  private final Recipe recipe1_nullId = new Recipe(null, name1, Set.of());

  private final Recipe recipe2 = new Recipe(uuid2, name2, Set.of(ingredient1));
  private final RecipeDto recipeDto2 = new RecipeDto(uuid2, name2, List.of(ingredientDto1));
  private final RecipeCreateDto recipeCreateDto2 = new RecipeCreateDto(name2, List.of(uuid3));
  private final Recipe recipe2_nullId = new Recipe(null, name2, Set.of(ingredient1));

  private final List<Recipe> recipes = List.of(recipe1, recipe2);
  private final List<RecipeDto> recipesDtos = List.of(recipeDto1, recipeDto2);

  @Test
  @DisplayName("Returns all recipes")
  void getRecipes() throws Exception {
    when(recipeService.getAllRecipes()).thenReturn(recipes);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipes"))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(equalTo(objectMapper.writeValueAsString(recipesDtos))));
  }

  @Nested
  @DisplayName("Get recipe by id")
  class GetRecipeById {
    @Test
    @DisplayName("Return recipe with given id that is in database")
    void getRecipeById_recipeWithIdInDatabase_returnsRecipe() throws Exception {
      doReturn(recipe1).when(recipeService).findById(uuid1);

      mockMvc
          .perform(MockMvcRequestBuilders.get("/recipes/" + uuid1))
          .andExpect(status().isOk())
          .andExpect(
              MockMvcResultMatchers.content()
                  .string(equalTo(objectMapper.writeValueAsString(recipeDto1))));
    }

    @Test
    @DisplayName("Throw NotFoundException if no recipe with given id is in database")
    void getRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() throws Exception {
      doThrow(NotFoundException.class).when(recipeService).findById(uuid1);

      mockMvc
          .perform(MockMvcRequestBuilders.get("/recipes/" + uuid1))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Add recipe")
  class AddRecipe {
    @Nested
    @DisplayName("No ingredients")
    class AddRecipe_noIngredients {
      @Test
      @DisplayName("Valid recipe, name is unique")
      void addRecipeNoIngredients_validRecipeNameUnique_returnsSavedRecipe() throws Exception {
        doReturn(recipe1_nullId).when(recipeCreateDtoMapper).apply(recipeCreateDto1);
        doReturn(recipe1).when(recipeService).addRecipe(recipe1_nullId);

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipeCreateDto1)))
            .andExpect(status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(equalTo((objectMapper.writeValueAsString(recipeDto1)))));
      }

      @Test
      @DisplayName("Valid recipe, name is not unique")
      void addRecipeNoIngredients_validRecipeNameNotUnique_returns400() throws Exception {
        doReturn(recipe1_nullId).when(recipeCreateDtoMapper).apply(recipeCreateDto1);
        doThrow(UniqueConstraintViolationException.class)
            .when(recipeService)
            .addRecipe(recipe1_nullId);

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipeCreateDto1)))
            .andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("Has ingredients")
    class AddRecipe_hasIngredients {
      @Test
      @DisplayName("Valid recipe, name is unique")
      void addRecipeHasIngredients_validRecipeNameUnique_returnsSavedRecipe() throws Exception {
        doReturn(recipe2_nullId).when(recipeCreateDtoMapper).apply(recipeCreateDto2);
        doReturn(recipe2).when(recipeService).addRecipe(recipe2_nullId);

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipeCreateDto2)))
            .andExpect(status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(equalTo((objectMapper.writeValueAsString(recipeDto2)))));
      }

      @Test
      @DisplayName("Valid recipe, name is not unique")
      void addRecipeHasIngredients_validRecipeNameNotUnique_returns400() throws Exception {
        doReturn(recipe2_nullId).when(recipeCreateDtoMapper).apply(recipeCreateDto2);
        doThrow(UniqueConstraintViolationException.class)
            .when(recipeService)
            .addRecipe(recipe2_nullId);

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(recipeCreateDto2)))
            .andExpect(status().isBadRequest());
      }
    }
  }
}
