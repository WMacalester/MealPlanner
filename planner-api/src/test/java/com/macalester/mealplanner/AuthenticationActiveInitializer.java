package com.macalester.mealplanner;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class AuthenticationActiveInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static final String PROPERTY_FIRST_VALUE = "true";

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "authentication.toggle=true");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "authentication.jwt.secretKey=a16af43fb94395491d4d27d5d913ec3d82024887d7edb44124ff45b9da058b53");
    }
}
