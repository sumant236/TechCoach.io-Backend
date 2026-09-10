package com.techcoach.dto.ai;

import lombok.Data;

/**
 * DTO for mapping the AI's JSON evaluation response into a structured format.
 */
@Data
public class EvaluationResponse {
    private String feedback;
    private int score;
}
