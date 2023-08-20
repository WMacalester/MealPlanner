package com.macalester.mealplanner.auth;

public record AuthenticationResponse(String accessToken, String refreshToken) {
}
