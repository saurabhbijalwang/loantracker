package com.loantracker;

import com.loantracker.config.AppConfig;
import com.loantracker.dto.CreateLoanRequest;
import com.loantracker.exception.LoanTrackerException;
import com.loantracker.service.LoanService;
import com.loantracker.util.ConnectionPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Main application class for LoanTracker.
 * Responsible for application initialization and lifecycle management.
 */
public class LoanTrackerApp {
    private static final Logger logger = LoggerFactory.getLogger(LoanTrackerApp.class);

    public static void main(String[] args) {
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║  Starting {} v{}         ║", AppConfig.APP_NAME, AppConfig.APP_VERSION);
        logger.info("╚════════════════════════════════════════╝");

        try {
            // Log configuration
            AppConfig.logConfiguration();

            // Initialize database schema
            initializeDatabase();
            logger.info("Database schema initialized successfully");

            // Create and start application service
            LoanService loanService = new LoanService();
            runDemonstration(loanService);

            logger.info("╔════════════════════════════════════════╗");
            logger.info("║  {} Application started successfully  ║", AppConfig.APP_NAME);
            logger.info("╚════════════════════════════════════════╝");

        } catch (LoanTrackerException e) {
            logger.error("LoanTracker Error [{}]: {}", e.getErrorCode(), e.getUserMessage(), e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error during application startup", e);
            System.exit(1);
        } finally {
            // Graceful shutdown
            shutdownApplication();
        }
    }

    /**
     * Initialize database schema with proper error handling.
     *
     * @throws LoanTrackerException if initialization fails
     */
    private static void initializeDatabase() throws LoanTrackerException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables if not exist
            String[] ddlStatements = {
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

            for (String sql : ddlStatements) {
                stmt.execute(sql);
            }

            logger.debug("Database tables created/verified successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database schema", e);
            throw new LoanTrackerException(
                com.loantracker.exception.ErrorCode.INITIALIZATION_FAILED,
                "Database initialization failed",
                e
            );
        }
    }

    /**
     * Demonstrate application functionality with sample data.
     *
     * @param loanService the loan service
     * @throws LoanTrackerException if operation fails
     */
    private static void runDemonstration(LoanService loanService) throws LoanTrackerException {
        logger.info("═══ Running Demonstration ═══");

        try {
            // Create a sample loan
            CreateLoanRequest request = new CreateLoanRequest(
                "John Doe",
                new BigDecimal("50000.00"),
                new BigDecimal("5.5"),
                60,
                LocalDate.of(2026, 1, 1)
            );

            logger.info("Creating sample loan...");
            var loanResponse = loanService.createLoan(request);
            logger.info("✓ Loan created: ID={}, Borrower={}, Amount={}", 
                loanResponse.getId(), loanResponse.getBorrowerName(), loanResponse.getPrincipalAmount());

            // Retrieve and display all loans
            var allLoans = loanService.getAllLoans();
            logger.info("✓ Retrieved {} loans from database", allLoans.size());

            // Display summary
            String summary = loanService.getSummary();
            logger.info("✓ Summary: {}", summary);

        } catch (LoanTrackerException e) {
            logger.error("Error during demonstration: {}", e.getUserMessage());
            throw e;
        }
    }

    /**
     * Graceful application shutdown.
     */
    private static void shutdownApplication() {
        logger.info("Shutting down {}...", AppConfig.APP_NAME);
        ConnectionPoolManager.shutdown();
        logger.info("{} shut down complete", AppConfig.APP_NAME);
    }
}
