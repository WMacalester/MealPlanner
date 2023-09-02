package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.validator.PasswordConstraint;
import com.macalester.mealplanner.validator.UsernameConstraint;

public record AuthenticationRequest(
        @UsernameConstraint
        String username,
        @PasswordConstraint
        String password) {
}
