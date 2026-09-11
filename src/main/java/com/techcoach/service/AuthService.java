package com.techcoach.service;

import com.techcoach.dto.auth.AuthRequest;
import com.techcoach.dto.auth.AuthResponse;
import com.techcoach.dto.common.UserDto;
import com.techcoach.entity.User;
import com.techcoach.enums.Role;
import com.techcoach.exception.UnauthorizedException;
import com.techcoach.exception.UserAlreadyExistsException;
import com.techcoach.exception.UserNotFoundException;
import com.techcoach.repository.UserRepository;
import com.techcoach.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Orchestrates user authentication, registration, and session context management using Spring Security and JWTs.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // Registers a new user, hashes their password, and immediately issues a JWT to support auto-login flows
    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
        return new AuthResponse(token, userDto);
    }

    // Authenticates user credentials via Spring Security's AuthenticationManager and generates a session JWT
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtil.generateToken(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();

        return new AuthResponse(token, userDto);
    }

    // Extracts the authenticated principal from the current SecurityContext and safely maps it to a secure UserDto
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated!");
        }

        Object principal = authentication.getPrincipal();

        // Explicitly guard against Spring Security's default anonymous user token to prevent ClassCastExceptions
        if ("anonymousUser".equals(principal)) {
            throw new UnauthorizedException("Anonymous access is not allowed!");
        }

        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exists in the database!"));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
