package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.auth.jwt.JwtService;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.user.User;
import com.macalester.mealplanner.user.UserRepository;
import com.macalester.mealplanner.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        return new AuthenticationResponse(jwtService.generateToken(user));
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));

        User user = userRepository.findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s already exists", authenticationRequest.password())));

        return new AuthenticationResponse(jwtService.generateToken(user));
    }
}
