package com.techcoach.exception;

/**
 * Custom exception thrown when a user lookup by identifier (like email or ID) yields no results.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
