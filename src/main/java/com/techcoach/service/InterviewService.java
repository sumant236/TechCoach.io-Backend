package com.techcoach.service;

import com.techcoach.dto.ai.EvaluationRequest;
import com.techcoach.dto.ai.EvaluationResponse;
import com.techcoach.dto.ai.GeneratedQuestionsResponse;
import com.techcoach.dto.interview.*;
import com.techcoach.entity.Interview;
import com.techcoach.entity.QnARecord;
import com.techcoach.entity.User;
import com.techcoach.enums.Status;
import com.techcoach.exception.UserNotFoundException;
import com.techcoach.repository.InterviewRepository;
import com.techcoach.repository.QnARecordRepository;
import com.techcoach.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orchestrates the core business logic for interview sessions, synchronizing database persistence with external AI evaluation services.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final QnARecordRepository qnARecordRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    // Initializes an interview session by persisting the parent record first to ensure foreign key integrity for AI-generated questions
    public InterviewResponse startInterview(InterviewStartRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Throws UserNotFoundException immediately if the user was deleted from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Interview interview = Interview.builder()
                .user(user)
                .role(request.getRole())
                .techStack(request.getTechStack())
                .experienceLevel(request.getExperienceLevel())
                .status(Status.IN_PROGRESS)
                .build();

        interview = interviewRepository.save(interview);

        GeneratedQuestionsResponse aiResponse = aiService.generateQuestions(
                request.getRole(), request.getTechStack(), request.getExperienceLevel()
        );

        List<QuestionDto> questionDtos = new ArrayList<>();
        if (aiResponse != null && aiResponse.getQuestions() != null) {
            for (String qText : aiResponse.getQuestions()) {
                QnARecord record = QnARecord.builder()
                        .interview(interview)
                        .questionText(qText)
                        .build();

                record = qnARecordRepository.save(record);

                questionDtos.add(QuestionDto.builder()
                        .id(record.getId())
                        .questionText(record.getQuestionText())
                        .build());
            }
        }

        return InterviewResponse.builder()
                .id(interview.getId())
                .status(interview.getStatus().name())
                .role(interview.getRole())
                .techStack(interview.getTechStack())
                .questions(questionDtos)
                .build();
    }

    // Evaluates a candidate's answer via AI and updates the existing QnA record to maintain session state
    public EvaluationResponse submitAnswerAndEvaluate(Long interviewId, AnswerSubmitRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + interviewId));

        QnARecord record = interview.getQnaRecords().stream()
                .filter(q -> q.getQuestionText().equals(request.getQuestionText()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this interview session"));

        EvaluationRequest aiRequest = new EvaluationRequest();
        aiRequest.setQuestionText(request.getQuestionText());
        aiRequest.setUserAnswer(request.getUserAnswer());
        aiRequest.setJobRole(interview.getRole());
        aiRequest.setExperience(interview.getExperienceLevel());

        EvaluationResponse aiResponse = aiService.evaluateAnswer(aiRequest);

        record.setUserAnswer(request.getUserAnswer());
        record.setAiFeedback(aiResponse.getFeedback());
        record.setScore(aiResponse.getScore());

        qnARecordRepository.save(record);

        return aiResponse;
    }

    // Rehydrates the complete interview session state and associated QnA records for frontend resumption
    public InterviewResponse getInterviewById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        List<QuestionDto> questionDtos = interview.getQnaRecords().stream()
                .map(record -> QuestionDto.builder()
                        .id(record.getId())
                        .questionText(record.getQuestionText())
                        .userAnswer(record.getUserAnswer())
                        .aiFeedback(record.getAiFeedback())
                        .score(record.getScore())
                        .build())
                .collect(Collectors.toList());

        return InterviewResponse.builder()
                .id(interview.getId())
                .status(interview.getStatus().name())
                .role(interview.getRole())
                .techStack(interview.getTechStack())
                .questions(questionDtos)
                .build();
    }

    // Aggregates user interview history and computes rolling average scores for dashboard analytics
    public List<InterviewHistoryDto> getUserInterviewHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Interview> interviews = interviewRepository.findByUserOrderByCreatedAtDesc(user);

        return interviews.stream().map(interview -> {
            double avgScore = 0.0;
            if (!interview.getQnaRecords().isEmpty()) {
                double totalScore = interview.getQnaRecords().stream()
                        .filter(q -> q.getScore() != null)
                        .mapToInt(QnARecord::getScore)
                        .sum();
                long gradedQuestions = interview.getQnaRecords().stream()
                        .filter(q -> q.getScore() != null)
                        .count();

                if (gradedQuestions > 0) {
                    avgScore = totalScore / gradedQuestions;
                }
            }

            return InterviewHistoryDto.builder()
                    .id(interview.getId())
                    .role(interview.getRole())
                    .techStack(interview.getTechStack())
                    .experienceLevel(interview.getExperienceLevel())
                    .date(interview.getCreatedAt() != null ? interview.getCreatedAt().toString() : "N/A")
                    .averageScore(Math.round(avgScore * 10.0) / 10.0)
                    .build();
        }).collect(Collectors.toList());
    }
}