package com.techcoach.dto.ai;

import lombok.Data;

import java.util.List;

/**
 * DTO for mapping the generated list of questions from the AI service.
 */
@Data
public class GeneratedQuestionsResponse {
    List<String> questions;
}