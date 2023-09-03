package com.macalester.mealplanner.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDtoMapper;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class IngredientsIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired private IngredientDtoMapper ingredientDtoMapper;

    private static final String name1 = "ingredient a";
    private static final String name2 = "ingredient b";
    private static final String name3 = "ingredient c";
    private static final String newName_request = "    ingredient   new name  ";
    private static final String newName = "ingredient new name";
    private static final Ingredient ingredient1 = new Ingredient(UUID.randomUUID(), name1, null);
    private static final IngredientCreateDto ingredientCreateDto1 = new IngredientCreateDto(newName_request);
    private static final IngredientCreateDto ingredientCreateDto_nameAlreadyExists = new IngredientCreateDto(name1);
    private static final IngredientCreateDto ingredientCreateDto_invalid = new IngredientCreateDto(null);

    private static final Ingredient ingredient2 = new Ingredient(UUID.randomUUID(), name2, null);
    private static final Ingredient ingredient3 = new Ingredient(UUID.randomUUID(), name3, null);

    @AfterEach
    void teardown() {
        ingredientRepository.deleteAll();
    }

    @Nested
    @DisplayName("Get all ingredients")
    class GetAllIngredientsTest {

        @Nested
        @DisplayName("JSON Accept Header")
        @WithMockUser(roles = "USER")
        class JsonTest {
            @Test
            @DisplayName("Get all ingredients returns all ingredients in database")
            void getAllIngredients_givenIngredientsInDb_returnsAllIngredients() throws Exception {
                ingredientRepository.save(ingredient1);
                ingredientRepository.save(ingredient2);
                ingredientRepository.save(ingredient3);

                String responseBody =
                        mockMvc
                                .perform(MockMvcRequestBuilders.get("/ingredients"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                List<Ingredient> responseIngredients =
                        objectMapper.readValue(responseBody, new TypeReference<>() {
                        });
                assertEquals(
                        List.of(name1, name2, name3),
                        responseIngredients.stream().map(Ingredient::getName).collect(Collectors.toList()));
            }

            @Test
            @DisplayName("Get all ingredients returns empty list if no ingredients in database")
            @WithMockUser(roles = "USER")
            void getAllIngredients_givenNoIngredientsInDb_returnsEmptyList() throws Exception {
                mockMvc
                        .perform(MockMvcRequestBuilders.get("/ingredients"))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string(equalTo("[]")));
            }

            @Test
            @DisplayName("User is admin, returns 200")
            @WithMockUser(roles = "ADMIN")
            void getAllIngredients_userIsAdmin_returnsEmptyList() throws Exception {
                mockMvc
                        .perform(MockMvcRequestBuilders.get("/ingredients"))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("User does not have USER role, returns 403")
            @WithMockUser(roles = "")
            void getAllIngredients_userNotAtLeastUserRole_returns403() throws Exception {
                mockMvc
                        .perform(MockMvcRequestBuilders.get("/ingredients"))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("CSV Accept Header")
        class CsvTest {
            @Test
            @DisplayName("Get all ingredients returns all ingredients in database in csv format")
            @WithMockUser(roles = "ADMIN")
            void getAllIngredients_givenIngredientsInDb_returnsAllIngredientsInCSV() throws Exception {
                ingredientRepository.save(ingredient1);
                ingredientRepository.save(ingredient2);
                ingredientRepository.save(ingredient3);

                String responseBody =
                        mockMvc
                                .perform(MockMvcRequestBuilders.get("/ingredients").accept("text/csv"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String expected = String.join("\n", name1, name2, name3);

                assertEquals(expected, responseBody);
            }

            @Test
            @DisplayName("User has role User, returns 403")
            @WithMockUser(roles = "USER")
            void getAllIngredients_userHasRoleUser_returns403() throws Exception {
                mockMvc
                        .perform(MockMvcRequestBuilders.get("/ingredients").accept("text/csv"))
                        .andExpect(status().isForbidden());
            }
        }
    }

    @Nested
    @DisplayName("Adding a new ingredient")
    class AddNewIngredientTest {
        @Test
        @DisplayName("Posting valid ingredient with unique name returns saved ingredient")
        @WithMockUser(roles = "ADMIN")
        void addNewIngredient_validIngredientWithUniqueName_returnsSavedIngredient() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/ingredients")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(newName))
                    .andExpect(jsonPath("$.id").isNotEmpty());
        }

        @Test
        @DisplayName("Posting valid ingredient with a non-unique name returns 400")
        @WithMockUser(roles = "ADMIN")
        void addNewIngredient_validIngredientWithNonUniqueName_returns400() throws Exception {
            ingredientRepository.save(ingredient1);
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/ingredients")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientCreateDto_nameAlreadyExists)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Posting Invalid ingredient returns 400")
        @WithMockUser(roles = "ADMIN")
        void addNewIngredient_invalidIngredient_returns400() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/ingredients")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientCreateDto_invalid)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("User has role User, returns 403")
        @WithMockUser(roles = "USER")
        void addNewIngredient_userHasRoleUser_returns403() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post("/ingredients")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientCreateDto1)))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Get ingredient by Id")
    class GetIngredientByIdTest {
        @Test
        @DisplayName("Returns ingredientDto with id in db")
        @WithMockUser(roles = "USER")
        void getIngredientById_givenIngredientWithIdInDb_returnsIngredient() throws Exception {
            Ingredient savedIngredient = ingredientRepository.save(ingredient1);
            String expected = objectMapper.writeValueAsString(ingredientDtoMapper.apply(savedIngredient));

            mockMvc
                    .perform(MockMvcRequestBuilders.get("/ingredients/" + savedIngredient.getId()))
                    .andExpect(status().isOk())
                    .andExpect(
                            MockMvcResultMatchers.content()
                                    .string(equalTo(expected)));
        }

        @Test
        @DisplayName("Returns 404 for ingredient with id not in db")
        @WithMockUser(roles = "USER")
        void getIngredientById_givenIngredientWithIdNotInDb_returns404() throws Exception {
            mockMvc
                    .perform(MockMvcRequestBuilders.get("/ingredients/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    @DisplayName("Edit ingredient by id")
    class EditIngredientByIdTest {

        private static final String newIngredientName = "new ingredient name";
        private final IngredientEditDto ingredientEditDto = new IngredientEditDto(newIngredientName);
        private final IngredientEditDto ingredientEditDto_blankName =
                new IngredientEditDto("        \n  ");

        @Test
        @DisplayName("Name in request is unique, returns ingredient with updated name")
        @WithMockUser(roles = "ADMIN")
        void editIngredientById_givenUniqueName_returnsIngredientWithUpdatedName() throws Exception {
            Ingredient ingredientSaved = ingredientRepository.save(ingredient1);
            ingredientSaved.setName(newIngredientName);
            String expected = objectMapper.writeValueAsString(ingredientDtoMapper.apply(ingredientSaved));

            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch("/ingredients/" + ingredientSaved.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientEditDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(equalTo(expected)));
        }

        @Test
        @DisplayName("Name in request is not unique, returns 400")
        @WithMockUser(roles = "ADMIN")
        void editIngredientById_givenNameAndIsNotUnique_returns400() throws Exception {
            Ingredient ingredientSaved = ingredientRepository.save(ingredient1);
            IngredientEditDto ingredientEditDto_nonUniqueName = new IngredientEditDto(ingredientSaved.getName());

            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch("/ingredients/" + ingredientSaved.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientEditDto_nonUniqueName)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Request ingredient id not in db returns 404")
        @WithMockUser(roles = "ADMIN")
        void editIngredientById_givenIngredientWithIdNotInDb_returns404() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch("/ingredients/" + UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientEditDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Name in request is blank, returns 400")
        @WithMockUser(roles = "ADMIN")
        void editIngredientById_givenBlankName_returns400() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch("/ingredients/" + UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientEditDto_blankName)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("User has role user, returns 403")
        @WithMockUser(roles = "USER")
        void editIngredientById_userHasRoleUser_returns403() throws Exception {
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch("/ingredients/" + UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ingredientEditDto)))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Delete ingredient by Id")
    class DeleteIngredientByIdTest {
        @Test
        @DisplayName("Delete ingredient by id")
        @WithMockUser(roles = "ADMIN")
        void deleteIngredientById_givenUUIDAndIngredientInDb_returns204() throws Exception {
            ingredientRepository.save(ingredient1);

            mockMvc
                    .perform(MockMvcRequestBuilders.delete("/ingredients/" + ingredient1.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("User has role User, returns 403")
        @WithMockUser(roles = "USER")
        void deleteIngredientById_givenUserHasRoleUser_returns403() throws Exception {
            ingredientRepository.save(ingredient1);

            mockMvc
                    .perform(MockMvcRequestBuilders.delete("/ingredients/" + ingredient1.getId()))
                    .andExpect(status().isForbidden());
        }
    }
}
