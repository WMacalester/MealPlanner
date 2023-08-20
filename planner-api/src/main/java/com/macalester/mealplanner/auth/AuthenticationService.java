package com.macalester.mealplanner.auth;

import static com.macalester.mealplanner.auth.jwt.JwtAuthenticationFilter.BEARER_PREFIX;

import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(value = "authentication.toggle", havingValue = "true")
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerUser(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByUsername(userRegisterDto.username())) {
            throw new UniqueConstraintViolationException(String.format("User with username %s already exists", userRegisterDto.username()));
        }

        User user = User.builder()
                .username(userRegisterDto.username())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);

        return new AuthenticationResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));

        User user = userRepository.findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s already exists", authenticationRequest.password())));

        return new AuthenticationResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header for refresh token was not given");
        }

        String refreshToken = authHeader.substring(BEARER_PREFIX.length());
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            UserDetails user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(String.format("User with username %s was not found", username)));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                return new AuthenticationResponse(accessToken, refreshToken);
            }
        }

        throw new IllegalArgumentException("Refresh token was not valid");
    }
}
