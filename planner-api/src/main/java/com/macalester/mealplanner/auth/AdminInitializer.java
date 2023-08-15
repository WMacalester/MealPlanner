package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value = "admin.username")
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean initialised = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialised) {
            return;
        }

        log.info("Initialising admin");

        if (username.isBlank()) {
            log.error("Admin user name was not provided, aborting admin initialisation");
            return;
        }

        String encodedPassword = password != null ? passwordEncoder.encode(password) : passwordEncoder.encode("");

        User admin = new User(null, username, encodedPassword, UserRole.ADMIN);
        userRepository.save(admin);

        initialised = true;
    }
}
