package com.techcoach.controller;

import com.techcoach.dto.ai.EvaluationResponse;
import com.techcoach.dto.common.ApiResponse;
import com.techcoach.dto.interview.AnswerSubmitRequest;
import com.techcoach.dto.interview.InterviewHistoryDto;
import com.techcoach.dto.interview.InterviewResponse;
import com.techcoach.dto.interview.InterviewStartRequest;
import com.techcoach.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller managing the lifecycle of interview sessions, including initialization, answer evaluation, and history retrieval.
 */
@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // Initializes a new interview session and generates the initial set of role-specific questions
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<InterviewResponse>> startInterview(@RequestBody @Valid InterviewStartRequest request) {
        InterviewResponse responseData = interviewService.startInterview(request);

        ApiResponse<InterviewResponse> response = ApiResponse.<InterviewResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Interview session created successfully")
                .data(responseData)
                .build();

        return ResponseEntity.ok(response);
    }

    // Processes a user's answer, orchestrates synchronous AI evaluation, and persists the feedback
    @PostMapping("/{interviewId}/answer")
    public ResponseEntity<ApiResponse<EvaluationResponse>> saveAnswer(
            @PathVariable Long interviewId,
            @RequestBody @Valid AnswerSubmitRequest request) {

        EvaluationResponse evaluationData = interviewService.submitAnswerAndEvaluate(interviewId, request);

        ApiResponse<EvaluationResponse> response = ApiResponse.<EvaluationResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Answer evaluated and saved successfully")
                .data(evaluationData)
                .build();

        return ResponseEntity.ok(response);
    }

    // Retrieves the complete context and QnA history of a specific interview session
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> getInterview(@PathVariable Long id) {
        InterviewResponse responseData = interviewService.getInterviewById(id);

        ApiResponse<InterviewResponse> response = ApiResponse.<InterviewResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Interview retrieved successfully")
                .data(responseData)
                .build();

        return ResponseEntity.ok(response);
    }

    // Fetches the summarized interview history for the currently authenticated user's dashboard
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<InterviewHistoryDto>>> getInterviewHistory() {
        List<InterviewHistoryDto> history = interviewService.getUserInterviewHistory();

        ApiResponse<List<InterviewHistoryDto>> response = ApiResponse.<List<InterviewHistoryDto>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Interview history retrieved successfully")
                .data(history)
                .build();

        return ResponseEntity.ok(response);
    }
}
