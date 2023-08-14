package com.macalester.mealplanner.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.macalester.mealplanner.AuthenticationActiveInitializer;
import com.macalester.mealplanner.auth.UserRegisterDto;
import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ContextConfiguration(initializers = AuthenticationActiveInitializer.class)
public class AuthenticationIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/auth";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    private static final String username1 = "User 1";
    private static final String password1 = "password 1";
    private User user1 = new User(null, username1, password1, UserRole.USER);

    @AfterEach
    void teardown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Register User")
    class RegisterUserTest {
        @Test
        @DisplayName("Username is already registered returns 400")
        void registerUser_usernameNotUnique_returns400() throws Exception {
            user1 = userRepository.save(user1);
            UserRegisterDto userRegisterDto = new UserRegisterDto(username1, password1);
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(BASE_URL + "/register")
                                    .contentType(MediaType.APPLICATION_JSON).
                                    content(objectMapper.writeValueAsString(userRegisterDto))
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("User with unique username is registered and returns 200")
        void registerUser_usernameUnique_registersUser() throws Exception {
            UserRegisterDto userRegisterDto = new UserRegisterDto(username1, password1);
            String responseBody =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.post(BASE_URL + "/register")
                                            .contentType(MediaType.APPLICATION_JSON).
                                            content(objectMapper.writeValueAsString(userRegisterDto))
                            )
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            String expected = String.format("{\"token\":\"%s\"}",jwtService.generateToken(user1));

            assertEquals(expected, responseBody);
        }
    }
}
