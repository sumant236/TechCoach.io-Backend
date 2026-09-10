package com.techcoach.repository;

import com.techcoach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techcoach.entity.Interview;

import java.util.List;

/**
 * Repository interface for Interview entity operations.
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    // Retrieves user's interview history, automatically sorted by the most recent first for the dashboard
    List<Interview> findByUserOrderByCreatedAtDesc(User user);
}
