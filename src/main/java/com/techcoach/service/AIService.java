package com.techcoach.service;

import com.techcoach.dto.ai.EvaluationRequest;
import com.techcoach.dto.ai.EvaluationResponse;
import com.techcoach.dto.ai.GeneratedQuestionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates communication with the external Gemini AI API to generate interview questions and evaluate candidate answers.
 */
@Service
@RequiredArgsConstructor
public class AIService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=";

    // Generates a randomized set of technical interview questions based on the candidate's profile
    public GeneratedQuestionsResponse generateQuestions(String jobRole, String techStack, String experienceLevel) {
        String requestId = java.util.UUID.randomUUID().toString();

        String prompt = String.format(
                "You are a technical interviewer. Generate 3 to 5 frequently asked, fundamental interview questions for a %s role with experience level %s focusing on %s. The questions MUST cover core concepts, mostly-asked industry standard topics, and common practical scenarios. DO NOT generate rare, overly obscure, or highly advanced niche questions. To prevent repetition, use this Randomization Seed: %s to shuffle and select a different mix of these common questions each time. You MUST respond ONLY with a valid JSON object strictly following this exact schema: {\"questions\": [\"question 1\", \"question 2\", \"question 3\"]}. Do not include any numbered lists, conversational text, or markdown formatting like ```json.",
                jobRole, experienceLevel, techStack, requestId
        );

        try {
            String rawResponse = executeGeminiRequest(prompt);
            String cleanJson = cleanJsonResponse(rawResponse);
            return objectMapper.readValue(cleanJson, GeneratedQuestionsResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI generated questions response: " + e.getMessage());
        }
    }

    // Navigates the deeply nested Gemini JSON response structure to extract the generated text content
    private String extractTextFromResponse(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new RuntimeException("Error parsing AI response structure: " + e.getMessage());
        }
    }

    // Evaluates a candidate's answer against the original question and returns a scored JSON response
    public EvaluationResponse evaluateAnswer(EvaluationRequest request) {
        String prompt = String.format(
                "You are a strict technical interviewer. Evaluate the candidate's answer based on the question. " +
                        "Job Role: %s, Experience: %s. " +
                        "Question: %s " +
                        "Candidate's Answer: %s " +
                        "You MUST respond ONLY with a valid JSON object containing exactly two keys: 'score' (an integer out of 10) and 'feedback' (a string with your detailed review and the ideal answer). Do not include markdown formatting like ```json.",
                request.getJobRole(), request.getExperience(), request.getQuestionText(), request.getUserAnswer()
        );

        try {
            String rawResponse = executeGeminiRequest(prompt);
            String cleanJson = cleanJsonResponse(rawResponse);
            return objectMapper.readValue(cleanJson, EvaluationResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI evaluation response: " + e.getMessage());
        }
    }

    // Constructs the specific nested JSON payload required by the Gemini API and executes the synchronous HTTP request
    private String executeGeminiRequest(String prompt) {
        String url = GEMINI_API_BASE_URL + geminiApiKey;

        /*
         * Constructing the nested JSON payload expected by Gemini API:
         * {
         *   "contents": [
         *     {
         *       "parts": [
         *         {
         *           "text": "You are an expert technical interviewer..."
         *         }
         *       ]
         *     }
         *   ],
         *   "generationConfig": {
         *     "temperature": 0.7,
         *     "maxOutputTokens": 4096
         *   }
         * }
         */

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 4096);
        requestBody.put("generationConfig", generationConfig);

        try {
            ResponseEntity<Map> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(Map.class);

            return extractTextFromResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Gemini request: " + e.getMessage());
        }
    }

    // Strips markdown code blocks (e.g., ```json) commonly returned by LLMs to ensure Jackson can parse the raw string
    private String cleanJsonResponse(String rawResponse) {
        if (rawResponse != null) {
            return rawResponse.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
        }
        return rawResponse;
    }
}
