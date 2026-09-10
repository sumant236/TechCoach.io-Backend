package com.techcoach.dto.interview;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for submitting a user's answer to a specific interview question.
 */
@Data
public class AnswerSubmitRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "User answer is required")
    private String userAnswer;
}