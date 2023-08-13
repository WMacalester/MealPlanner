package com.macalester.mealplanner.ingredients;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macalester.mealplanner.AuthOpenSecurityConfig;
import com.macalester.mealplanner.dataexporter.DataExporter;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDtoMapper;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDtoMapper;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(IngredientController.class)
@ContextConfiguration(classes = {AuthOpenSecurityConfig.class})
class IngredientControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private IngredientService ingredientService;
  @MockBean private DataExporter dataExporter;
    @SpyBean private IngredientDtoMapper ingredientDtoMapper;
    @SpyBean
    private IngredientCreateDtoMapper ingredientCreateDtoMapper;

    private static final UUID uuid1 = UUID.randomUUID();
    private static final UUID uuid2 = UUID.randomUUID();
    private static final String name1 = "ingredient a";
    private static final String name2 = "ingredient b";
    private final Ingredient ingredient1 = new Ingredient(uuid1, name1, null);
    private final Ingredient ingredient2 = new Ingredient(uuid2, name2, null);
    private final Ingredient ingredient1_NullId = new Ingredient(null, name1, null);
    private final IngredientCreateDto ingredientCreateDto1 = new IngredientCreateDto(name1);
    private final List<Ingredient> ingredients = List.of(ingredient1, ingredient2);
    private final IngredientDto ingredientDto1 = new IngredientDto(uuid1, name1);
    private final IngredientDto ingredientDto2 = new IngredientDto(uuid2, name2);
    private final List<IngredientDto> ingredientDtos = List.of(ingredientDto1, ingredientDto2);

  @Nested
  @DisplayName("Get all ingredients")
  class GetAllIngredientsTest {
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
  class GetIngredientByIdTest {
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
  class AddIngredientTest {
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

  @Nested
  @DisplayName("Edit ingredient by id")
  class EditIngredientByIdTest {

    private final IngredientEditDto ingredientEditDto = new IngredientEditDto("abc");
    private final IngredientEditDto ingredientEditDto_blankName =
        new IngredientEditDto("        \n  ");

    @Test
    @DisplayName("Name in request is unique, returns ingredient with updated name")
    void editIngredientById_givenUniqueName_returnsIngredientWithUpdatedName() throws Exception {
      doReturn(ingredient1).when(ingredientService).editIngredientById(uuid1, ingredientEditDto);

      mockMvc
          .perform(
              MockMvcRequestBuilders.patch("/ingredients/" + uuid1)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientEditDto)))
          .andExpect(status().isOk())
          .andExpect(content().string(equalTo(objectMapper.writeValueAsString(ingredientDto1))));
    }

    @Test
    @DisplayName("Name in request is not unique, returns 400")
    void editIngredientById_givenNameAndIsNotUnique_returns400() throws Exception {
      doThrow(UniqueConstraintViolationException.class)
          .when(ingredientService)
          .editIngredientById(uuid1, ingredientEditDto);

      mockMvc
          .perform(
              MockMvcRequestBuilders.patch("/ingredients/" + uuid1)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientEditDto)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request ingredient id not in db returns 404")
    void editIngredientById_givenIngredientWithIdNotInDb_returns404() throws Exception {
      IngredientEditDto ingredientEditDto = new IngredientEditDto("abc");
      doThrow(NotFoundException.class)
          .when(ingredientService)
          .editIngredientById(uuid1, ingredientEditDto);

      mockMvc
          .perform(
              MockMvcRequestBuilders.patch("/ingredients/" + uuid1)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientEditDto)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Name in request is blank, returns 400")
    void editIngredientById_givenBlankName_returns400() throws Exception {

      mockMvc
          .perform(
              MockMvcRequestBuilders.patch("/ingredients/" + uuid1)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ingredientEditDto_blankName)))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  @DisplayName("Delete ingredient by id")
  void deleteIngredientById_givenUUID_returns204() throws Exception {
    doNothing().when(ingredientService).deleteById(uuid1);
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/ingredients/" + uuid1))
        .andExpect(status().isNoContent());
  }
}
