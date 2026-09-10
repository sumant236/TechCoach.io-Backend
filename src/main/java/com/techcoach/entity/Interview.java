package com.techcoach.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.techcoach.enums.Status;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an interview session.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.LAZY used to prevent N+1 query performance issues
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String techStack;

    @Column(nullable = false)
    private String experienceLevel;

    // Cascade all operations to QnA records
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevent infinite recursion during JSON serialization
    @Builder.Default
    private List<QnARecord> qnaRecords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}