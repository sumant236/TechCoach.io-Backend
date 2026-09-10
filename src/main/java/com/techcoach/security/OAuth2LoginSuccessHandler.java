package com.techcoach.security;

import com.techcoach.entity.User;
import com.techcoach.enums.Role;
import com.techcoach.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles successful OAuth2 authentication (e.g., Google login) by provisioning new users if needed,
 * generating a JWT, and redirecting the client with a secure HttpOnly cookie.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // React frontend URL, defaults to localhost:5173
    @Value("${frontend.url:https://techcoach-io.onrender.com}")
    private String frontendUrl;

    // Intercepts successful OAuth2 logins, persists first-time users, attaches the JWT cookie, and redirects to the frontend dashboard
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Auto-registers first-time social login users, injecting a random UUID password to satisfy entity constraints securely
        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .password(java.util.UUID.randomUUID().toString())
                    .role(Role.USER)
                    .build();
            return userRepository.save(newUser);
        });

        String token = jwtUtil.generateToken(email);

        // Sets an HttpOnly cookie to securely pass the JWT to the client while mitigating XSS vulnerabilities
        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true) // Set to true in production (HTTPS)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("none")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        response.sendRedirect(frontendUrl + "/dashboard");
    }
}
