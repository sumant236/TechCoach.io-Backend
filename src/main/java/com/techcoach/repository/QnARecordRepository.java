package com.techcoach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techcoach.entity.QnARecord;

import java.util.List;

/**
 * Repository interface for QnARecord entity operations.
 */
@Repository
public interface QnARecordRepository extends JpaRepository<QnARecord, Long> {
    List<QnARecord> findByInterviewId(Long interviewId);
}
