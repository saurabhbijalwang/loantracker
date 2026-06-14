package com.loantracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application configuration constants and properties.
 * Centralizes all configuration to follow DRY principle.
 */
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    // Database Configuration
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:./data/loantracker";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "";
    
    // Connection Pool Configuration
    public static final int DB_POOL_SIZE = 10;
    public static final int DB_POOL_MAX_LIFETIME = 1800000; // 30 minutes
    public static final int DB_POOL_IDLE_TIMEOUT = 600000;   // 10 minutes
    public static final int DB_POOL_CONNECTION_TIMEOUT = 30000; // 30 seconds

    // Application Configuration
    public static final String APP_NAME = "LoanTracker";
    public static final String APP_VERSION = "1.0.0";
    
    // Validation Constants
    public static final int MAX_BORROWER_NAME_LENGTH = 255;
    public static final int MIN_LOAN_TERM_MONTHS = 1;
    public static final int MAX_LOAN_TERM_MONTHS = 360; // 30 years
    
    private AppConfig() {
        // Prevent instantiation
    }

    /**
     * Log configuration on startup (excluding sensitive data)
     */
    public static void logConfiguration() {
        logger.info("=== {} v{} Configuration ===", APP_NAME, APP_VERSION);
        logger.info("Database: {}", DB_URL);
        logger.info("Connection Pool Size: {}", DB_POOL_SIZE);
        logger.info("=== Configuration Loaded ===");
    }
}
