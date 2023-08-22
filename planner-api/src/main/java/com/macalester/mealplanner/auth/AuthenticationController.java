package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@ConditionalOnProperty(value = "authentication.toggle", havingValue = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto, HttpServletResponse response) {
        try {
            authenticationService.registerUser(userRegisterDto, response);
        } catch (UniqueConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public void authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        authenticationService.authenticateUser(authenticationRequest, response);
    }

    @GetMapping(value = "/refresh-token")
    public void refreshtoken(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.refreshToken(request, response);
    }
}
