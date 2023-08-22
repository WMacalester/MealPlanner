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
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    public void registerUser(UserRegisterDto userRegisterDto, HttpServletResponse response) {
        if (userRepository.existsByUsername(userRegisterDto.username())) {
            throw new UniqueConstraintViolationException(String.format("User with username %s already exists", userRegisterDto.username()));
        }

        User user = User.builder()
                .username(userRegisterDto.username())
                .password(passwordEncoder.encode(userRegisterDto.password()))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);

        response.addCookie(cookieFromJwt(JwtToken.ACCESS_TOKEN.getHeaderName(), jwtService.generateToken(user)));
        response.addCookie(cookieFromJwt(JwtToken.REFRESH_TOKEN.getHeaderName(), jwtService.generateRefreshToken(user)));
    }

    public void authenticateUser(AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));

        User user = userRepository.findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s already exists", authenticationRequest.password())));

        response.addCookie(cookieFromJwt(JwtToken.ACCESS_TOKEN.getHeaderName(), jwtService.generateToken(user)));
        response.addCookie(cookieFromJwt(JwtToken.REFRESH_TOKEN.getHeaderName(), jwtService.generateRefreshToken(user)));
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshCookie = AuthUtils.getTokenFromRequest(request, JwtToken.REFRESH_TOKEN);

        if (refreshCookie.isEmpty()){
            return;
        }

        String refreshToken = refreshCookie.get().getValue();
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            UserDetails user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(String.format("User with username %s was not found", username)));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                response.addCookie(cookieFromJwt(JwtToken.ACCESS_TOKEN.getHeaderName(), accessToken));
            }
        }
    }

    private static Cookie cookieFromJwt(String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        return cookie;
    }
}
