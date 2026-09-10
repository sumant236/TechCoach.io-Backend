package com.techcoach.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Generic wrapper for all REST API responses to maintain a consistent structure.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Hides any fields from the JSON response if their value is null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private boolean success;
    private String message;

    // Holds the actual success payload (e.g., AuthResponse, InterviewResponse)
    private T data;

    // Contains field-level validation errors, if any
    private Map<String, String> errors;
}