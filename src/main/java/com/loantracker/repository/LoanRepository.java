package com.loantracker.repository;

import com.loantracker.model.Loan;
import com.loantracker.exception.ErrorCode;
import com.loantracker.exception.LoanTrackerException;
import com.loantracker.util.ConnectionPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Loan entity operations.
 * Implements Data Access Object (DAO) pattern for database interactions.
 * Centralizes all SQL operations and provides a clean abstraction layer.
 */
public class LoanRepository {
    private static final Logger logger = LoggerFactory.getLogger(LoanRepository.class);

    private static final String INSERT_LOAN = 
        "INSERT INTO loans (borrower_name, principal_amount, interest_rate, loan_term_months, start_date) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID = 
        "SELECT id, borrower_name, principal_amount, interest_rate, loan_term_months, start_date, status, created_at " +
        "FROM loans WHERE id = ?";

    private static final String SELECT_ALL = 
        "SELECT id, borrower_name, principal_amount, interest_rate, loan_term_months, start_date, status, created_at " +
        "FROM loans ORDER BY created_at DESC";

    private static final String UPDATE_STATUS = 
        "UPDATE loans SET status = ? WHERE id = ?";

    private static final String DELETE_LOAN = 
        "DELETE FROM loans WHERE id = ?";

    private static final String COUNT_BY_STATUS = 
        "SELECT COUNT(*) FROM loans WHERE status = ?";

    /**
     * Create a new loan in the database.
     *
     * @param loan the loan to create
     * @return the loan with generated ID
     * @throws LoanTrackerException if operation fails
     */
    public Loan create(Loan loan) throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LOAN, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, loan.getBorrowerName());
            stmt.setBigDecimal(2, loan.getPrincipalAmount());
            stmt.setBigDecimal(3, loan.getInterestRate());
            stmt.setInt(4, loan.getLoanTermMonths());
            stmt.setDate(5, Date.valueOf(loan.getStartDate()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating loan failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getInt(1));
                    logger.info("Loan created successfully with ID: {}", loan.getId());
                    return loan;
                } else {
                    throw new SQLException("Creating loan failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to create loan for borrower: {}", loan.getBorrowerName(), e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to create loan in database",
                e
            );
        }
    }

    /**
     * Retrieve a loan by ID.
     *
     * @param id the loan ID
     * @return Optional containing the loan if found
     * @throws LoanTrackerException if operation fails
     */
    public Optional<Loan> findById(int id) throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    logger.debug("Loan found with ID: {}", id);
                    return Optional.of(loan);
                }
            }
            logger.debug("Loan not found with ID: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Failed to retrieve loan with ID: {}", id, e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to retrieve loan from database",
                e
            );
        }
    }

    /**
     * Retrieve all loans.
     *
     * @return list of all loans
     * @throws LoanTrackerException if operation fails
     */
    public List<Loan> findAll() throws LoanTrackerException {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = ConnectionPoolManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            logger.debug("Retrieved {} loans from database", loans.size());
            return loans;
        } catch (SQLException e) {
            logger.error("Failed to retrieve all loans", e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to retrieve loans from database",
                e
            );
        }
    }

    /**
     * Update loan status.
     *
     * @param loanId the loan ID
     * @param status the new status
     * @throws LoanTrackerException if operation fails or loan not found
     */
    public void updateStatus(int loanId, String status) throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {

            stmt.setString(1, status);
            stmt.setInt(2, loanId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Loan not found for update with ID: {}", loanId);
                throw new LoanTrackerException(
                    ErrorCode.LOAN_NOT_FOUND,
                    "Loan not found with ID: " + loanId
                );
            }
            logger.info("Loan {} status updated to: {}", loanId, status);
        } catch (SQLException e) {
            logger.error("Failed to update loan status for ID: {}", loanId, e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to update loan status",
                e
            );
        }
    }

    /**
     * Delete a loan.
     *
     * @param loanId the loan ID to delete
     * @throws LoanTrackerException if operation fails
     */
    public void delete(int loanId) throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_LOAN)) {

            stmt.setInt(1, loanId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Loan not found for deletion with ID: {}", loanId);
                throw new LoanTrackerException(
                    ErrorCode.LOAN_NOT_FOUND,
                    "Loan not found with ID: " + loanId
                );
            }
            logger.info("Loan deleted with ID: {}", loanId);
        } catch (SQLException e) {
            logger.error("Failed to delete loan with ID: {}", loanId, e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to delete loan",
                e
            );
        }
    }

    /**
     * Count loans by status.
     *
     * @param status the status to count
     * @return number of loans with given status
     * @throws LoanTrackerException if operation fails
     */
    public int countByStatus(String status) throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_BY_STATUS)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Failed to count loans by status: {}", status, e);
            throw new LoanTrackerException(
                ErrorCode.DB_OPERATION_FAILED,
                "Failed to count loans",
                e
            );
        }
    }

    /**
     * Map ResultSet row to Loan object.
     *
     * @param rs the ResultSet
     * @return mapped Loan object
     * @throws SQLException if mapping fails
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBorrowerName(rs.getString("borrower_name"));
        loan.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
        loan.setInterestRate(rs.getBigDecimal("interest_rate"));
        loan.setLoanTermMonths(rs.getInt("loan_term_months"));
        loan.setStartDate(rs.getDate("start_date").toLocalDate());
        loan.setStatus(rs.getString("status"));
        return loan;
    }
}
