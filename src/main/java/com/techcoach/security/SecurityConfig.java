package com.techcoach.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central security configuration class that establishes the security filter chain,
 * stateless session management, CORS policies, and authentication providers.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${frontend.url}")
    private String frontendUrl;

    // Configures the security filter chain, defining public/protected routes, CSRF, CORS, and filter ordering
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as we use stateless JWTs stored in cookies
                .csrf(csrf -> csrf.disable())
                // Configure CORS policy to allow credentials from the React frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configure endpoint authorization rules (Specific rules must come before broad wildcards)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/me").authenticated() // Explicitly protect /me before general auth routes
                        .requestMatchers("/api/auth/**").permitAll() // Publicly accessible login/register routes
                        .anyRequest().authenticated() // Protect all other application routes
                )
                // Return 401 Unauthorized for unauthenticated API requests instead of redirecting
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // Enable OAuth2 social login and wire custom success handler
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                // Enforce stateless session management (no HTTP sessions created)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Inject JWT filter before the standard Spring UsernamePassword filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Explicitly allow the React frontend URL
        configuration.setAllowedOrigins(List.of(frontendUrl));

        // Allow standard HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Crucial: Allow cookies to be sent back and forth across origins
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply this CORS configuration to all backend endpoints
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}