package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.auth.jwt.JwtToken;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public User registerUser(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByUsername(userRegisterDto.username())) {
            throw new UniqueConstraintViolationException(String.format("User with username %s already exists", userRegisterDto.username()));
        }

        User user = User.builder()
                .username(userRegisterDto.username())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .role(UserRole.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));
        return userRepository.findByUsername(authenticationRequest.username());
    }

    public Optional<User> refreshToken(HttpServletRequest request) {
        Optional<Cookie> refreshCookie = AuthUtils.getTokenFromRequest(request, JwtToken.REFRESH_TOKEN);

        if (refreshCookie.isEmpty() || refreshCookie.get().getValue().isEmpty()){
            return Optional.empty();
        }

        String refreshToken = refreshCookie.get().getValue();
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(String.format("User with username %s was not found", username)));

            if (jwtService.isTokenValid(refreshToken, user)) {
                return Optional.of(user);

            }
        }

        return Optional.empty();
    }
}
