package com.techcoach.dto.interview;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing an individual interview question along with the user's answer and AI evaluation.
 */
@Data
@Builder
public class QuestionDto {
    private Long id;
    private String questionText;
    private String userAnswer;
    private String aiFeedback;
    private Integer score;
}