package com.techcoach.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Entity representing individual QnA records for an interview.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnARecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    // TEXT column definition used to bypass standard VARCHAR(255) limits for long AI payloads
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Min(0)
    @Max(10)
    private Integer score;
}
