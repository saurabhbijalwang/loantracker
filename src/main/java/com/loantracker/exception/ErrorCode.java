package com.loantracker.exception;

/**
 * Enumeration of application error codes.
 * Maps business errors to specific codes for better error handling and monitoring.
 */
public enum ErrorCode {
    // Database errors
    DB_CONNECTION_FAILED("DB_001", "Failed to establish database connection"),
    DB_OPERATION_FAILED("DB_002", "Database operation failed"),
    RECORD_NOT_FOUND("DB_003", "Requested record not found"),
    
    // Validation errors
    INVALID_LOAN_DATA("VAL_001", "Invalid loan data provided"),
    INVALID_PAYMENT_DATA("VAL_002", "Invalid payment data provided"),
    BORROWER_NAME_REQUIRED("VAL_003", "Borrower name is required"),
    INVALID_PRINCIPAL_AMOUNT("VAL_004", "Principal amount must be positive"),
    INVALID_INTEREST_RATE("VAL_005", "Interest rate must be between 0 and 100"),
    INVALID_LOAN_TERM("VAL_006", "Loan term must be between 1 and 360 months"),
    
    // Business logic errors
    LOAN_NOT_FOUND("BIZ_001", "Loan not found"),
    LOAN_NOT_ACTIVE("BIZ_002", "Loan is not in active status"),
    PAYMENT_EXCEEDS_REMAINING("BIZ_003", "Payment amount exceeds remaining loan balance"),
    
    // System errors
    INITIALIZATION_FAILED("SYS_001", "Application initialization failed"),
    INTERNAL_ERROR("SYS_999", "Internal server error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
