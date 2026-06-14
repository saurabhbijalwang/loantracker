package com.loantracker.service;

import com.loantracker.dto.CreateLoanRequest;
import com.loantracker.dto.LoanResponse;
import com.loantracker.model.Loan;
import com.loantracker.repository.LoanRepository;
import com.loantracker.exception.ErrorCode;
import com.loantracker.exception.LoanTrackerException;
import com.loantracker.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for loan operations.
 * Handles business logic, validation, and coordination with repository.
 * Implements Single Responsibility and Dependency Injection principles.
 */
public class LoanService {
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    private final LoanRepository loanRepository;

    public LoanService() {
        this.loanRepository = new LoanRepository();
    }

    /**
     * Create a new loan with validation.
     *
     * @param request the create loan request
     * @return the created loan response
     * @throws LoanTrackerException if validation or creation fails
     */
    public LoanResponse createLoan(CreateLoanRequest request) throws LoanTrackerException {
        logger.info("Creating new loan for borrower: {}", request.getBorrowerName());

        // Validate all inputs
        ValidationUtils.validateBorrowerName(request.getBorrowerName());
        ValidationUtils.validatePrincipalAmount(request.getPrincipalAmount());
        ValidationUtils.validateInterestRate(request.getInterestRate());
        ValidationUtils.validateLoanTerm(request.getLoanTermMonths());
        ValidationUtils.validateStartDate(request.getStartDate());

        // Create loan entity
        Loan loan = new Loan(
            request.getBorrowerName(),
            request.getPrincipalAmount(),
            request.getInterestRate(),
            request.getLoanTermMonths(),
            request.getStartDate()
        );

        // Persist to database
        Loan createdLoan = loanRepository.create(loan);
        logger.info("Loan successfully created with ID: {}", createdLoan.getId());

        return convertToResponse(createdLoan);
    }

    /**
     * Retrieve a loan by ID.
     *
     * @param loanId the loan ID
     * @return the loan response
     * @throws LoanTrackerException if loan not found
     */
    public LoanResponse getLoan(int loanId) throws LoanTrackerException {
        logger.debug("Retrieving loan with ID: {}", loanId);

        Optional<Loan> loan = loanRepository.findById(loanId);
        if (loan.isEmpty()) {
            logger.warn("Loan not found with ID: {}", loanId);
            throw new LoanTrackerException(
                ErrorCode.LOAN_NOT_FOUND,
                "Loan not found with ID: " + loanId
            );
        }

        return convertToResponse(loan.get());
    }

    /**
     * Retrieve all loans.
     *
     * @return list of loan responses
     * @throws LoanTrackerException if retrieval fails
     */
    public List<LoanResponse> getAllLoans() throws LoanTrackerException {
        logger.debug("Retrieving all loans");
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Close a loan.
     *
     * @param loanId the loan ID to close
     * @throws LoanTrackerException if loan not found or operation fails
     */
    public void closeLoan(int loanId) throws LoanTrackerException {
        logger.info("Closing loan with ID: {}", loanId);

        // Verify loan exists and is active
        LoanResponse loan = getLoan(loanId);
        if (!"ACTIVE".equals(loan.getStatus())) {
            logger.warn("Cannot close non-active loan. Current status: {}", loan.getStatus());
            throw new LoanTrackerException(
                ErrorCode.LOAN_NOT_ACTIVE,
                "Cannot close loan that is not in ACTIVE status. Current status: " + loan.getStatus()
            );
        }

        loanRepository.updateStatus(loanId, "CLOSED");
        logger.info("Loan {} successfully closed", loanId);
    }

    /**
     * Get summary statistics.
     *
     * @return summary string
     * @throws LoanTrackerException if retrieval fails
     */
    public String getSummary() throws LoanTrackerException {
        logger.debug("Generating loan summary statistics");
        int activeLoanCount = loanRepository.countByStatus("ACTIVE");
        int closedLoanCount = loanRepository.countByStatus("CLOSED");
        
        return String.format("Active Loans: %d, Closed Loans: %d", activeLoanCount, closedLoanCount);
    }

    /**
     * Convert Loan entity to LoanResponse DTO.
     *
     * @param loan the loan entity
     * @return the response DTO
     */
    private LoanResponse convertToResponse(Loan loan) {
        return new LoanResponse(
            loan.getId(),
            loan.getBorrowerName(),
            loan.getPrincipalAmount(),
            loan.getInterestRate(),
            loan.getLoanTermMonths(),
            loan.getStartDate(),
            loan.getStatus()
        );
    }
}
