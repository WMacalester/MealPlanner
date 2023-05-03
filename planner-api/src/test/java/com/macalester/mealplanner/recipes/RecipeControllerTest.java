package com.macalester.mealplanner.recipes;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RecipeService recipeService;

  private final Recipe recipe1 = new Recipe(UUID.randomUUID(), "recipe 1", null);
  private final Recipe recipe2 = new Recipe(UUID.randomUUID(), "recipe 2", null);

  private final List<Recipe> recipes = List.of(recipe1, recipe2);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Returns all recipes")
  void getRecipes() throws Exception {
    when(recipeService.getAllRecipes()).thenReturn(recipes);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipes/"))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(equalTo(objectMapper.writeValueAsString(recipes))));
  }
}
