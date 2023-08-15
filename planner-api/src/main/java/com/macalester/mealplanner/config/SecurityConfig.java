package com.macalester.mealplanner.config;

import com.macalester.mealplanner.auth.jwt.JwtAuthenticationFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@ConditionalOnProperty(value = "authentication.toggle", havingValue = "true")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenicationProvider;
    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//            For h2 database
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")){
            httpSecurity.headers().frameOptions().disable(); // for h2
            httpSecurity.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/h2/**"))
                    .permitAll();
        }

        httpSecurity.csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers( "/auth/**")
                .permitAll()
                .requestMatchers("/error").anonymous() // allows exceptions to map to http codes other than 403
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenicationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
