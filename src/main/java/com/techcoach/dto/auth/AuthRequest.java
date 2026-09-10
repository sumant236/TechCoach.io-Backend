package com.techcoach.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user authentication (login and registration) requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
