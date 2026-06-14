package com.loantracker;

import com.loantracker.dto.CreateLoanRequest;
import com.loantracker.exception.ErrorCode;
import com.loantracker.exception.LoanTrackerException;
import com.loantracker.service.LoanService;
import com.loantracker.util.ConnectionPoolManager;
import com.loantracker.util.ValidationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for LoanTracker application.
 * Tests validation, business logic, and error handling.
 */
public class LoanTrackerAppTest {
    private LoanService loanService;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Initialize test database schema once for all tests
        try (Connection conn = ConnectionPoolManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop existing tables if they exist (fresh state for tests)
            String[] dropStatements = {
                "DROP TABLE IF EXISTS payments",
                "DROP TABLE IF EXISTS loans"
            };

            for (String sql : dropStatements) {
                try {
                    stmt.execute(sql);
                } catch (Exception e) {
                    // Tables might not exist, that's ok
                }
            }

            // Create tables for testing
            String[] createStatements = {
                "CREATE TABLE IF NOT EXISTS loans (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  borrower_name VARCHAR(255) NOT NULL," +
                "  principal_amount DECIMAL(10, 2) NOT NULL," +
                "  interest_rate DECIMAL(5, 2) NOT NULL," +
                "  loan_term_months INT NOT NULL," +
                "  start_date DATE NOT NULL," +
                "  status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CLOSED', 'DEFAULTED'))," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  INDEX idx_status (status)," +
                "  INDEX idx_borrower (borrower_name)" +
                ")",
                "CREATE TABLE IF NOT EXISTS payments (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  loan_id INT NOT NULL," +
                "  payment_amount DECIMAL(10, 2) NOT NULL," +
                "  payment_date DATE NOT NULL," +
                "  notes VARCHAR(500)," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE," +
                "  INDEX idx_loan_id (loan_id)," +
                "  INDEX idx_payment_date (payment_date)" +
                ")"
            };

            for (String sql : createStatements) {
                stmt.execute(sql);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        loanService = new LoanService();
        
        // Clean loans table before each test (but keep schema)
        try (Connection conn = ConnectionPoolManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM payments");
            stmt.execute("DELETE FROM loans");
        }
    }

    @After
    public void tearDown() {
        // Cleanup after each test if needed
    }

    // ─────────────────────────────────────────────────────────────
    // Validation Tests
    // ─────────────────────────────────────────────────────────────

    @Test
    public void testValidateBorrowerNameSuccess() {
        try {
            ValidationUtils.validateBorrowerName("John Doe");
            assertTrue("Validation should pass for valid name", true);
        } catch (LoanTrackerException e) {
            fail("Should not throw exception for valid name");
        }
    }

    @Test
    public void testValidateBorrowerNameEmpty() {
        try {
            ValidationUtils.validateBorrowerName("");
            fail("Should throw exception for empty name");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.BORROWER_NAME_REQUIRED, e.getErrorCode());
        }
    }

    @Test
    public void testValidateBorrowerNameNull() {
        try {
            ValidationUtils.validateBorrowerName(null);
            fail("Should throw exception for null name");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.BORROWER_NAME_REQUIRED, e.getErrorCode());
        }
    }

    @Test
    public void testValidatePrincipalAmountSuccess() {
        try {
            ValidationUtils.validatePrincipalAmount(new BigDecimal("50000.00"));
            assertTrue("Validation should pass for positive amount", true);
        } catch (LoanTrackerException e) {
            fail("Should not throw exception for positive amount");
        }
    }

    @Test
    public void testValidatePrincipalAmountZero() {
        try {
            ValidationUtils.validatePrincipalAmount(BigDecimal.ZERO);
            fail("Should throw exception for zero amount");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_PRINCIPAL_AMOUNT, e.getErrorCode());
        }
    }

    @Test
    public void testValidatePrincipalAmountNegative() {
        try {
            ValidationUtils.validatePrincipalAmount(new BigDecimal("-1000.00"));
            fail("Should throw exception for negative amount");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_PRINCIPAL_AMOUNT, e.getErrorCode());
        }
    }

    @Test
    public void testValidateInterestRateSuccess() {
        try {
            ValidationUtils.validateInterestRate(new BigDecimal("5.5"));
            assertTrue("Validation should pass for valid rate", true);
        } catch (LoanTrackerException e) {
            fail("Should not throw exception for valid rate");
        }
    }

    @Test
    public void testValidateInterestRateOutOfRange() {
        try {
            ValidationUtils.validateInterestRate(new BigDecimal("150.00"));
            fail("Should throw exception for rate > 100");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_INTEREST_RATE, e.getErrorCode());
        }
    }

    @Test
    public void testValidateLoanTermSuccess() {
        try {
            ValidationUtils.validateLoanTerm(60);
            assertTrue("Validation should pass for valid term", true);
        } catch (LoanTrackerException e) {
            fail("Should not throw exception for valid term");
        }
    }

    @Test
    public void testValidateLoanTermTooShort() {
        try {
            ValidationUtils.validateLoanTerm(0);
            fail("Should throw exception for term < 1");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_LOAN_TERM, e.getErrorCode());
        }
    }

    @Test
    public void testValidateLoanTermTooLong() {
        try {
            ValidationUtils.validateLoanTerm(361);
            fail("Should throw exception for term > 360");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_LOAN_TERM, e.getErrorCode());
        }
    }

    @Test
    public void testValidateStartDateInFuture() {
        try {
            ValidationUtils.validateStartDate(LocalDate.now().plusDays(1));
            fail("Should throw exception for future start date");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_LOAN_DATA, e.getErrorCode());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Loan Service Tests
    // ─────────────────────────────────────────────────────────────

    @Test
    public void testCreateLoanSuccess() {
        try {
            CreateLoanRequest request = new CreateLoanRequest(
                "Jane Smith",
                new BigDecimal("75000.00"),
                new BigDecimal("4.5"),
                84,
                LocalDate.of(2025, 1, 1)
            );

            var response = loanService.createLoan(request);
            assertNotNull("Loan should be created", response);
            assertNotNull("Loan ID should be generated", response.getId());
            assertEquals("Jane Smith", response.getBorrowerName());
            assertEquals("ACTIVE", response.getStatus());
        } catch (LoanTrackerException e) {
            fail("Should not throw exception: " + e.getUserMessage());
        }
    }

    @Test
    public void testCreateLoanWithInvalidData() {
        try {
            CreateLoanRequest request = new CreateLoanRequest(
                "Invalid User",
                new BigDecimal("-5000.00"), // Invalid: negative amount
                new BigDecimal("5.5"),
                60,
                LocalDate.now()
            );

            loanService.createLoan(request);
            fail("Should throw exception for invalid principal amount");
        } catch (LoanTrackerException e) {
            assertEquals(ErrorCode.INVALID_PRINCIPAL_AMOUNT, e.getErrorCode());
        }
    }

    @Test
    public void testGetAllLoans() {
        try {
            var loans = loanService.getAllLoans();
            assertNotNull("Loans list should not be null", loans);
            assertTrue("Loans list should be iterable", loans.size() >= 0);
        } catch (LoanTrackerException e) {
            fail("Should not throw exception: " + e.getUserMessage());
        }
    }

    @Test
    public void testGetSummary() {
        try {
            String summary = loanService.getSummary();
            assertNotNull("Summary should not be null", summary);
            assertTrue("Summary should contain 'Active Loans'", summary.contains("Active Loans"));
        } catch (LoanTrackerException e) {
            fail("Should not throw exception: " + e.getUserMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Error Handling Tests
    // ─────────────────────────────────────────────────────────────

    @Test
    public void testErrorCodeMapping() {
        assertNotNull("Error code should have description", ErrorCode.DB_CONNECTION_FAILED.getDescription());
        assertEquals("DB_001", ErrorCode.DB_CONNECTION_FAILED.getCode());
    }

    @Test
    public void testLoanTrackerExceptionToString() {
        LoanTrackerException exception = new LoanTrackerException(
            ErrorCode.LOAN_NOT_FOUND,
            "Loan with ID 999 not found"
        );
        
        String result = exception.toString();
        assertTrue("Exception string should contain error code", result.contains("BIZ_001"));
        assertTrue("Exception string should contain message", result.contains("not found"));
    }
}
