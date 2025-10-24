package com.ucc.Connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database connection manager using HikariCP.
 *
 * Configuration is read from environment variables:
 * - DB_URL
 * - DB_USER
 * - DB_PASS
 *
 * If env vars are not present, it will fall back to system properties:
 * - db.url, db.user, db.pass
 */
public final class DatabaseConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/sakila?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static HikariDataSource dataSource;

    private DatabaseConnection() {
        // utility class
    }

    private static synchronized void initDataSource() {
        if (dataSource != null) return;

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (url == null || user == null || pass == null) {
            // try system properties as fallback
            url = System.getProperty("db.url", DEFAULT_URL);
            user = System.getProperty("db.user", "root");
            pass = System.getProperty("db.pass", "");
            LOGGER.warn("DB environment variables not fully set; falling back to system properties or defaults. Avoid hard-coding credentials in production.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setPoolName("app-hikari-pool");
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        LOGGER.info("HikariCP DataSource initialized (pool={})", config.getPoolName());
    }

    public static Connection getInstanceConnection() throws SQLException {
        initDataSource();
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        initDataSource();
        return dataSource;
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            LOGGER.info("Shutting down HikariCP DataSource");
            dataSource.close();
            dataSource = null;
        }
    }
}
