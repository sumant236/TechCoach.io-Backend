package com.techcoach.exception;

/**
 * Custom exception thrown during registration if the provided email is already registered in the system.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
