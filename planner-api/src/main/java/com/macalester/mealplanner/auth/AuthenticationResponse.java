package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRole;

public record AuthenticationResponse(String username, UserRole userRole) {
    public AuthenticationResponse(User user){
        this(user.getUsername(), user.getRole());
    }
}
