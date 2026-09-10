package com.techcoach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techcoach.entity.User;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}