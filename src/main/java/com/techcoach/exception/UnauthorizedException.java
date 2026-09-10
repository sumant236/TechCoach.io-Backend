package com.techcoach.exception;

/**
 * Custom exception thrown when a user attempts to access a protected resource without valid authentication.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
