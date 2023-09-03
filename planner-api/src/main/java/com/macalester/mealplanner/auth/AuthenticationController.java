package com.macalester.mealplanner.auth;

import static com.macalester.mealplanner.auth.AuthUtils.generateCookieFromJwt;

import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.auth.jwt.JwtToken;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthenticationResponse registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto, HttpServletResponse response) {
        try {
            User user = authenticationService.registerUser(userRegisterDto);
            response.setStatus(HttpServletResponse.SC_OK);
            response.addCookie(generateCookieFromJwt(JwtToken.ACCESS_TOKEN, jwtService.generateToken(user)));
            response.addCookie(generateCookieFromJwt(JwtToken.REFRESH_TOKEN, jwtService.generateRefreshToken(user)));
            return new AuthenticationResponse(user);
        } catch (UniqueConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        Optional<User> user = authenticationService.authenticateUser(authenticationRequest);

        if (user.isPresent()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addCookie(generateCookieFromJwt(JwtToken.ACCESS_TOKEN, jwtService.generateToken(user.get())));
            response.addCookie(generateCookieFromJwt(JwtToken.REFRESH_TOKEN, jwtService.generateRefreshToken(user.get())));
            return new AuthenticationResponse(user.get());
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<User> user = authenticationService.refreshToken(request);

        if (user.isPresent()) {
            response.addCookie(generateCookieFromJwt(JwtToken.ACCESS_TOKEN, jwtService.generateToken(user.get())));
            response.setStatus(HttpServletResponse.SC_OK);
            return new AuthenticationResponse(user.get());
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie blankAccessCookie = generateCookieFromJwt(JwtToken.ACCESS_TOKEN, "");
        Cookie blankRefreshCookie = generateCookieFromJwt(JwtToken.REFRESH_TOKEN, "");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(blankAccessCookie);
        response.addCookie(blankRefreshCookie);
    }
}
