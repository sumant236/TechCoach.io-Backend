package com.techcoach.dto.ai;

import lombok.Data;

/**
 * Internal DTO used to pass question and user context to the AI service for evaluation.
 */
@Data
public class EvaluationRequest {
    private String questionText;
    private String userAnswer;
    private String jobRole;
    private String experience;
}