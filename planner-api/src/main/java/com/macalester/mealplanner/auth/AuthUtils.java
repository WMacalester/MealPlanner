package com.macalester.mealplanner.auth;

import com.macalester.mealplanner.auth.jwt.JwtToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class AuthUtils {
    private AuthUtils(){}

    public static Optional<Cookie> getTokenFromRequest(HttpServletRequest request, JwtToken tokenType){
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies).filter(e -> tokenType.getHeaderName().equals(e.getName())).findFirst();
    }

    public static Cookie generateCookieFromJwt(JwtToken tokenType, String token) {
        Cookie cookie = new Cookie(tokenType.getHeaderName(), token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath(tokenType.getPath());
        return cookie;
    }
}
