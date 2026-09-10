package com.techcoach.security;

import com.techcoach.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts incoming HTTP requests to extract and validate JWTs, establishing the Spring Security context
 * for stateless, token-based authentication.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    // Prioritizes token extraction from secure HttpOnly cookies to mitigate XSS vulnerabilities, with a fallback to standard Bearer headers
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        String userEmail = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            userEmail = jwtUtil.extractUsername(token);

            // Prevents redundant re-authentication and unnecessary database hits for already authenticated requests in the current thread
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // check if the user still exists in the database before authenticating the request
                if (userRepository.findByEmail(userEmail).isPresent()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                // If the user no longer exists in the database
                if (userRepository.findByEmail(userEmail).isEmpty()) {
                    Cookie invalidCookie = new Cookie("jwt_token", null);
                    invalidCookie.setHttpOnly(true);
                    invalidCookie.setPath("/");
                    invalidCookie.setMaxAge(0); // Instructs the browser to delete the cookie immediately
                    response.addCookie(invalidCookie);

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("User no longer exists. Please log in again.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}