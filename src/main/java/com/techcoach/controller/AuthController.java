package com.techcoach.controller;

import com.techcoach.dto.auth.AuthRequest;
import com.techcoach.dto.auth.AuthResponse;
import com.techcoach.dto.common.ApiResponse;
import com.techcoach.dto.common.UserDto;
import com.techcoach.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller handling user authentication flows and session management via HttpOnly cookies.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Registers a new user and securely logs them in by setting an HttpOnly JWT cookie
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody AuthRequest request, HttpServletResponse response) {
        AuthResponse authData = authService.register(request);

        // Attach token securely via cookie; avoid sending in JSON body
        response.addHeader(HttpHeaders.SET_COOKIE, createJwtCookie(authData.getToken(), 24 * 60 * 60).toString());

        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("User registered and logged in successfully")
                .data(authData.getUser())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // Authenticates user credentials and establishes a secure session via HttpOnly JWT cookie
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        AuthResponse authData = authService.login(request);
        System.out.println(authData);
        response.addHeader(HttpHeaders.SET_COOKIE, createJwtCookie(authData.getToken(), 24 * 60 * 60).toString());

        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Logged in successfully")
                .data(authData.getUser())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Terminates the user's active session by invalidating their authentication cookie
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // Invalidate session by setting cookie max-age to 0
        response.addHeader(HttpHeaders.SET_COOKIE, createJwtCookie(null, 0).toString());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Logged out successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // Retrieves the authenticated profile data for the currently logged-in user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto userDto = authService.getCurrentUser();

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Current user retrieved successfully")
                .data(userDto)
                .build();

        return ResponseEntity.ok(response);
    }

    // Helper method to construct secure, HttpOnly cookies for JWT storage
    private ResponseCookie createJwtCookie(String token, int maxAge) {
        return ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}