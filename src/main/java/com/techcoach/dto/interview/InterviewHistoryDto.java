package com.techcoach.dto.interview;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for transferring a user's summarized interview history to the frontend dashboard.
 */
@Data
@Builder
public class InterviewHistoryDto {
    private Long id;
    private String role;
    private String techStack;
    private String experienceLevel;

    // Formatted date string to avoid complex timezone parsing on the frontend
    private String date;

    private Double averageScore;
}