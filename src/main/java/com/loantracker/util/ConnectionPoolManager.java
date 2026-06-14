package com.loantracker.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.loantracker.config.AppConfig;
import com.loantracker.exception.ErrorCode;
import com.loantracker.exception.LoanTrackerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection pool manager using HikariCP.
 * Follows singleton pattern to ensure single pool instance.
 * Provides efficient connection management for database operations.
 */
public class ConnectionPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolManager.class);
    private static volatile HikariDataSource dataSource;
    private static final Object lock = new Object();

    private ConnectionPoolManager() {
        // Prevent instantiation
    }

    /**
     * Get or create the HikariCP data source (thread-safe singleton).
     *
     * @return configured HikariDataSource
     * @throws LoanTrackerException if initialization fails
     */
    public static DataSource getDataSource() throws LoanTrackerException {
        if (dataSource == null) {
            synchronized (lock) {
                if (dataSource == null) {
                    initializeDataSource();
                }
            }
        }
        return dataSource;
    }

    /**
     * Initialize HikariCP data source with configuration.
     *
     * @throws LoanTrackerException if initialization fails
     */
    private static void initializeDataSource() throws LoanTrackerException {
        try {
            logger.info("Initializing connection pool with HikariCP");
            
            // Explicitly load H2 driver
            Class.forName("org.h2.Driver");
            logger.debug("H2 Driver loaded successfully");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(AppConfig.DB_URL);
            config.setUsername(AppConfig.DB_USER);
            config.setPassword(AppConfig.DB_PASSWORD);
            config.setMaximumPoolSize(AppConfig.DB_POOL_SIZE);
            config.setMaxLifetime(AppConfig.DB_POOL_MAX_LIFETIME);
            config.setIdleTimeout(AppConfig.DB_POOL_IDLE_TIMEOUT);
            config.setConnectionTimeout(AppConfig.DB_POOL_CONNECTION_TIMEOUT);
            config.setAutoCommit(true);
            config.setPoolName("LoanTrackerPool");
            config.setLeakDetectionThreshold(60000); // 1 minute

            dataSource = new HikariDataSource(config);
            logger.info("Connection pool initialized successfully with {} max connections", AppConfig.DB_POOL_SIZE);
        } catch (ClassNotFoundException e) {
            logger.error("H2 driver not found in classpath", e);
            throw new LoanTrackerException(
                ErrorCode.DB_CONNECTION_FAILED,
                "H2 JDBC driver not found in classpath",
                e
            );
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new LoanTrackerException(
                ErrorCode.DB_CONNECTION_FAILED,
                "Failed to initialize database connection pool",
                e
            );
        }
    }

    /**
     * Get a connection from the pool.
     *
     * @return a database connection
     * @throws LoanTrackerException if connection cannot be obtained
     */
    public static Connection getConnection() throws LoanTrackerException {
        try {
            DataSource ds = getDataSource();
            return ds.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection from pool", e);
            throw new LoanTrackerException(
                ErrorCode.DB_CONNECTION_FAILED,
                "Failed to obtain database connection",
                e
            );
        }
    }

    /**
     * Shutdown the connection pool gracefully.
     * Call this during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down connection pool");
            dataSource.close();
            dataSource = null;
            logger.info("Connection pool shut down successfully");
        }
    }
}
