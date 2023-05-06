package com.macalester.mealplanner.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class IngredientsIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private IngredientRepository ingredientRepository;

  private static final String name1 = "Ingredient 1";
  private static final String name2 = "Ingredient 2";
  private static final String name3 = "Ingredient 3";
  private final Ingredient ingredient1 = new Ingredient(UUID.randomUUID(), name1, null);
  private final IngredientCreateDto ingredientCreateDto1 = new IngredientCreateDto(name1);
  private final IngredientCreateDto ingredientCreateDto_invalid = new IngredientCreateDto(null);

  private final Ingredient ingredient2 = new Ingredient(UUID.randomUUID(), name2, null);
  private final Ingredient ingredient3 = new Ingredient(UUID.randomUUID(), name3, null);

  @AfterEach
  void teardown() {
    ingredientRepository.deleteAll();
  }

  @Nested
  @DisplayName("Get all ingredients")
  class GetAllIngredients {
    @Test
    @DisplayName("Get all ingredients returns all ingredients in database")
    void getAllIngredients_givenIngredientsInDb_returnsAllIngredients() throws Exception {
      ingredientRepository.save(ingredient1);
      ingredientRepository.save(ingredient2);
      ingredientRepository.save(ingredient3);

      String responseBody =
          mockMvc
              .perform(MockMvcRequestBuilders.get("/ingredients"))
              .andDo(print())
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<Ingredient> responseIngredients =
          objectMapper.readValue(responseBody, new TypeReference<>() {});
      assertEquals(
          List.of(name1, name2, name3),
          responseIngredients.stream().map(Ingredient::getName).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Get all ingredients returns empty list if no ingredients in database")
    void getAllIngredients_givenNoIngredientsInDb_returnsEmptyList() throws Exception {
      mockMvc
          .perform(MockMvcRequestBuilders.get("/ingredients"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(MockMvcResultMatchers.content().string(equalTo("[]")));
    }
  }

  @Nested
  @DisplayName("Adding a new ingredient")
  class AddNewIngredient {
    @Test
    @DisplayName("Posting valid ingredient with unique name returns saved ingredient")
    void addNewIngredient_validIngredientWithUniqueName_returnsSavedIngredient() throws Exception {
      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value(name1))
          .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Posting valid ingredient with a non-unique name returns 400")
    void addNewIngredient_validIngredientWithNonUniqueName_returns400() throws Exception {
      ingredientRepository.save(ingredient1);
      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Posting Invalid ingredient returns 400")
    void addNewIngredient_invalidIngredient_returns400() throws Exception {
      ingredientRepository.save(ingredient1);
      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientCreateDto_invalid)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }
}
