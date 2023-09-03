package com.macalester.mealplanner.auth;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.macalester.mealplanner.BasePostgresContainer;
import com.macalester.mealplanner.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest()
@ContextConfiguration(
        initializers = {AdminInitializerTest.AdminInitialiser.class}
)
class AdminInitializerTest extends BasePostgresContainer {
    static final String USERNAME = "admin";
    static final String PASSWORD = "password";

    @Autowired
    private UserRepository userRepository;

    @AfterAll
    static void teardown(@Autowired UserRepository userRepository){
        userRepository.deleteAll();
    }

    @Test
    void onStart_adminLoadedAndSaved() {
        assertTrue(userRepository.existsByUsername("admin"));
    }

    static class AdminInitialiser implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "admin.username="+USERNAME);
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "admin.password="+PASSWORD);
        }
    }
}
