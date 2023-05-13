package com.macalester.mealplanner.ingredients;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private IngredientService ingredientService;
  private final IngredientMapper ingredientMapper = Mappers.getMapper(IngredientMapper.class);

  private static final UUID uuid1 = UUID.randomUUID();
  private static final UUID uuid2 = UUID.randomUUID();
  private final Ingredient ingredient1 = new Ingredient(uuid1, "ingredient 1", null);
  private final Ingredient ingredient2 = new Ingredient(uuid2, "ingredient 2", null);
  private final Ingredient ingredient1_NullId = new Ingredient(null, "ingredient 1", null);
  private final IngredientCreateDto ingredientCreateDto1 = new IngredientCreateDto("ingredient 1");
  private final List<Ingredient> ingredients = List.of(ingredient1, ingredient2);
  private final IngredientDto ingredientDto1 = ingredientMapper.ingredientToDto(ingredient1);
  private final List<IngredientDto> ingredientDtos = ingredientMapper.ingredientToDto(ingredients);

  @Nested
  @DisplayName("Get all ingredients")
  class GetAllIngredients {
    @Test
    void getAllIngredients_givenIngredientInDb_returnsList() throws Exception {
      doReturn(ingredients).when(ingredientService).findAll();

      mockMvc
          .perform(MockMvcRequestBuilders.get("/ingredients"))
          .andExpect(status().isOk())
          .andExpect(
              MockMvcResultMatchers.content()
                  .string(equalTo(objectMapper.writeValueAsString(ingredientDtos))));
    }
  }

  @Nested
  @DisplayName("Get ingredient by Id")
  class GetIngredientById {
    @Test
    @DisplayName("Returns ingredientDto with id in db")
    void getIngredientById_givenIngredientWithIdInDb_returnsIngredient() throws Exception {
      doReturn(ingredient1).when(ingredientService).findById(uuid1);

      mockMvc
          .perform(MockMvcRequestBuilders.get("/ingredients/" + uuid1))
          .andExpect(status().isOk())
          .andExpect(
              MockMvcResultMatchers.content()
                  .string(equalTo(objectMapper.writeValueAsString(ingredientDto1))));
    }

    @Test
    @DisplayName("Returns 404 for ingredient with id not in db")
    void getIngredientById_givenIngredientWithIdNotInDb_returns404() throws Exception {
      doThrow(NotFoundException.class).when(ingredientService).findById(uuid1);

      mockMvc
          .perform(MockMvcRequestBuilders.get("/ingredients/" + uuid1))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Add ingredient")
  class AddIngredient {
    @Test
    @DisplayName("Valid request returns saved ingredient")
    void addIngredient_validRequest_returnsSavedIngredient() throws Exception {
      doReturn(ingredient1).when(ingredientService).save(ingredient1_NullId);

      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
          .andExpect(status().isOk())
          .andExpect(
              MockMvcResultMatchers.content()
                  .string(equalTo(objectMapper.writeValueAsString(ingredientDto1))));
    }

    @Test
    @DisplayName("Invalid request returns 400")
    void addIngredient_invalidRequest_returns400() throws Exception {
      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new IngredientCreateDto(null))))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Valid request but name already exists returns 400")
    void addIngredient_validRequestButIngredientWithNameAlreadyExists_return400() throws Exception {
      doThrow(UniqueConstraintViolationException.class).when(ingredientService).save(ingredient1);
      mockMvc
          .perform(
              MockMvcRequestBuilders.post("/ingredients")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
          .andExpect(status().isBadRequest());
    }
  }
}
