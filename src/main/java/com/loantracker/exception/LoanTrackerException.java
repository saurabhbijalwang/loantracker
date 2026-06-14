package com.loantracker.exception;

/**
 * Custom exception for application-specific errors.
 * Provides consistent error handling and logging across the application.
 */
public class LoanTrackerException extends Exception {
    private final ErrorCode errorCode;
    private final String userMessage;

    public LoanTrackerException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = message;
    }

    public LoanTrackerException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, userMessage);
    }
}
