package com.macalester.mealplanner;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Used during development of authentication, enabling existing tests to maintain functionality as if not within feature flag
@TestConfiguration
public class AuthOpenSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf()
                .disable()
                .authorizeHttpRequests()
                .anyRequest()
                .permitAll();

        return httpSecurity.build();
    }
}
