package com.techcoach.dto.interview;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for initiating a new interview session.
 */
@Data
public class InterviewStartRequest {
    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Tech Stack is required")
    private String techStack;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;
}
