package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.validator.UsernameConstraint;

public record UserRegisterDto(
        @UsernameConstraint
        String username,
        String password) {
}
