package com.techcoach.dto.interview;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO for returning a complete interview session, including the generated questions.
 */
@Data
@Builder
public class InterviewResponse {
    private Long id;
    private String status;
    private String role;
    private String techStack;
    private List<QuestionDto> questions;
}