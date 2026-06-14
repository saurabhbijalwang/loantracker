package com.loantracker.util;

import com.loantracker.config.AppConfig;
import com.loantracker.exception.ErrorCode;
import com.loantracker.exception.LoanTrackerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Validation utility class for common validation operations.
 * Centralizes all validation logic following Single Responsibility Principle.
 */
public class ValidationUtils {
    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    private ValidationUtils() {
        // Prevent instantiation
    }

    /**
     * Validates borrower name.
     *
     * @param borrowerName the name to validate
     * @throws LoanTrackerException if validation fails
     */
    public static void validateBorrowerName(String borrowerName) throws LoanTrackerException {
        if (borrowerName == null || borrowerName.trim().isEmpty()) {
            logger.warn("Validation failed: borrower name is empty");
            throw new LoanTrackerException(
                ErrorCode.BORROWER_NAME_REQUIRED,
                "Borrower name is required and cannot be empty"
            );
        }

        if (borrowerName.length() > AppConfig.MAX_BORROWER_NAME_LENGTH) {
            logger.warn("Validation failed: borrower name exceeds maximum length");
            throw new LoanTrackerException(
                ErrorCode.INVALID_LOAN_DATA,
                String.format("Borrower name cannot exceed %d characters", AppConfig.MAX_BORROWER_NAME_LENGTH)
            );
        }
    }

    /**
     * Validates principal amount.
     *
     * @param principalAmount the amount to validate
     * @throws LoanTrackerException if validation fails
     */
    public static void validatePrincipalAmount(BigDecimal principalAmount) throws LoanTrackerException {
        if (principalAmount == null || principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Validation failed: principal amount is not positive");
            throw new LoanTrackerException(
                ErrorCode.INVALID_PRINCIPAL_AMOUNT,
                "Principal amount must be greater than zero"
            );
        }
    }

    /**
     * Validates interest rate.
     *
     * @param interestRate the rate to validate (0-100)
     * @throws LoanTrackerException if validation fails
     */
    public static void validateInterestRate(BigDecimal interestRate) throws LoanTrackerException {
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) < 0 || 
            interestRate.compareTo(new BigDecimal("100")) > 0) {
            logger.warn("Validation failed: interest rate is out of range");
            throw new LoanTrackerException(
                ErrorCode.INVALID_INTEREST_RATE,
                "Interest rate must be between 0 and 100 percent"
            );
        }
    }

    /**
     * Validates loan term in months.
     *
     * @param loanTermMonths the term to validate
     * @throws LoanTrackerException if validation fails
     */
    public static void validateLoanTerm(int loanTermMonths) throws LoanTrackerException {
        if (loanTermMonths < AppConfig.MIN_LOAN_TERM_MONTHS || 
            loanTermMonths > AppConfig.MAX_LOAN_TERM_MONTHS) {
            logger.warn("Validation failed: loan term is out of range");
            throw new LoanTrackerException(
                ErrorCode.INVALID_LOAN_TERM,
                String.format("Loan term must be between %d and %d months",
                    AppConfig.MIN_LOAN_TERM_MONTHS, AppConfig.MAX_LOAN_TERM_MONTHS)
            );
        }
    }

    /**
     * Validates start date is not in the future.
     *
     * @param startDate the date to validate
     * @throws LoanTrackerException if validation fails
     */
    public static void validateStartDate(LocalDate startDate) throws LoanTrackerException {
        if (startDate == null || startDate.isAfter(LocalDate.now())) {
            logger.warn("Validation failed: start date is in the future");
            throw new LoanTrackerException(
                ErrorCode.INVALID_LOAN_DATA,
                "Start date cannot be in the future"
            );
        }
    }

    /**
     * Validates payment amount.
     *
     * @param paymentAmount the amount to validate
     * @throws LoanTrackerException if validation fails
     */
    public static void validatePaymentAmount(BigDecimal paymentAmount) throws LoanTrackerException {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Validation failed: payment amount is not positive");
            throw new LoanTrackerException(
                ErrorCode.INVALID_PAYMENT_DATA,
                "Payment amount must be greater than zero"
            );
        }
    }
}
