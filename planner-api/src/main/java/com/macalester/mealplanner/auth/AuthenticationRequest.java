package com.macalester.mealplanner.auth;

public record AuthenticationRequest(
        String username,
        String password) {
}
