package com.macalester.mealplanner.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.macalester.mealplanner.auth.UserRegisterDto;
import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.auth.jwt.JwtToken;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class AuthenticationIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/auth";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    private static final String username1 = "User1";
    private static final String password1 = "password 1";
    private User user1 = new User(null, username1, password1, UserRole.ROLE_USER);

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
            Cookie[] responseCookies =
                    mockMvc
                            .perform(
                                    MockMvcRequestBuilders.post(BASE_URL + "/register")
                                            .contentType(MediaType.APPLICATION_JSON).
                                            content(objectMapper.writeValueAsString(userRegisterDto))
                            )
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getCookies();

            List<String> responseCookieValues = Arrays.stream(responseCookies).map(Cookie::getName).toList();

            Assertions.assertAll(
                    () -> assertTrue(responseCookieValues.contains(JwtToken.REFRESH_TOKEN.getHeaderName())),
                    () -> assertTrue(responseCookieValues.contains(JwtToken.ACCESS_TOKEN.getHeaderName()))
            );
        }

        @Test
        @DisplayName("User has non-alphanumeric characters and returns 400")
        void registerUser_usernameHasNonAlphaNumericCharacters_returns400() throws Exception {
            UserRegisterDto userRegisterDto = new UserRegisterDto("User6!", password1);
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(BASE_URL + "/register")
                                    .contentType(MediaType.APPLICATION_JSON).
                                    content(objectMapper.writeValueAsString(userRegisterDto))
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Password has illegal characters and returns 400")
        void registerUser_passwordHasIllegalCharacters_returns400() throws Exception {
            UserRegisterDto userRegisterDto = new UserRegisterDto("User", "password`[");
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.post(BASE_URL + "/register")
                                    .contentType(MediaType.APPLICATION_JSON).
                                    content(objectMapper.writeValueAsString(userRegisterDto))
                    )
                    .andExpect(status().isBadRequest());
        }
    }
}
