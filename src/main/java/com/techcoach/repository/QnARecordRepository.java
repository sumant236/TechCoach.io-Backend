package com.techcoach.repository;

import com.techcoach.entity.QnARecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for QnARecord entity operations.
 */
@Repository
public interface QnARecordRepository extends JpaRepository<QnARecord, Long> {

}
