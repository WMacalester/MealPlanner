package com.macalester.mealplanner.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macalester.mealplanner.AuthOpenSecurityConfig;
import com.macalester.mealplanner.AuthenticationActiveInitializer;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//@ContextConfiguration(classes = {AuthOpenSecurityConfig.class})
@ContextConfiguration(initializers = AuthenticationActiveInitializer.class)
public class IngredientsIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private IngredientRepository ingredientRepository;

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
            @WithMockUser(roles="ADMIN")
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

                String expected = String.join("\n", name1,name2,name3);

                assertEquals(expected, responseBody);
            }

            @Test
            @DisplayName("User has role User, returns 403")
            @WithMockUser(roles="USER")
            void getAllIngredients_userHasRoleUser_returns403V() throws Exception {
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
}
