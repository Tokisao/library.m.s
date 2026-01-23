package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    static {
        Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
        logger.setLevel(Level.WARNING);
    }

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String SERVER_URL = "jdbc:postgresql://localhost:2038/";
    private static final String LIBRARY_DB = "library_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "dayana1809";

    private static Connection connection = null;

    public static Connection getConnection() {
        return getConnection(LIBRARY_DB);
    }

    public static Connection getConnection(String databaseName) {
        if (connection == null || isConnectionClosed()) {
            try {
                Class.forName("org.postgresql.Driver");

                String url = SERVER_URL + databaseName;

                LOGGER.log(Level.FINE, "Attempting to connect to: " + url);
                LOGGER.log(Level.FINE, "User: " + USER);

                connection = DriverManager.getConnection(url, USER, PASSWORD);
                LOGGER.log(Level.FINE, "Successfully connected to database: " + databaseName);

            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", e);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Connection failed to database: " + databaseName, e);

                if (!databaseName.equals(LIBRARY_DB)) {
                    LOGGER.log(Level.FINE, "Attempting to connect to default library_db...");
                    return getConnection(LIBRARY_DB);
                }
            }
        }
        return connection;
    }

    public static Connection switchDatabase(String databaseName) {
        closeConnection();
        connection = null;
        return getConnection(databaseName);
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOGGER.log(Level.FINE, "Database connection closed.");
                }
                connection = null;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }

    public static void testConnection() {
        testConnection(LIBRARY_DB);
    }

    public static void testConnection(String databaseName) {
        try {
            Connection conn = getConnection(databaseName);
            if (conn != null && !conn.isClosed()) {
                LOGGER.log(Level.FINE, "Connection test PASSED for database: " + databaseName);

                try (var stmt = conn.createStatement();
                     var rs = stmt.executeQuery("SELECT current_database(), current_user, version()")) {

                    if (rs.next()) {
                        LOGGER.log(Level.FINE, "Current Database: " + rs.getString(1));
                        LOGGER.log(Level.FINE, "Current User: " + rs.getString(2));
                        LOGGER.log(Level.FINE, "PostgreSQL Version: " + rs.getString(3).split(",")[0]);
                    }
                }

            } else {
                LOGGER.log(Level.SEVERE, "Connection test FAILED for database: " + databaseName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection test failed for " + databaseName, e);
        }
    }

    public static boolean checkDatabaseExists(String databaseName) {
        Connection tempConn = null;
        try {
            String url = SERVER_URL + "postgres";
            tempConn = DriverManager.getConnection(url, USER, PASSWORD);

            String sql = "SELECT 1 FROM pg_database WHERE datname = ?";
            try (var stmt = tempConn.prepareStatement(sql)) {
                stmt.setString(1, databaseName);
                try (var rs = stmt.executeQuery()) {
                    boolean exists = rs.next();
                    LOGGER.log(Level.FINE, "Database '" + databaseName + "' exists: " + exists);
                    return exists;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking database existence", e);
            return false;
        } finally {
            if (tempConn != null) {
                try {
                    tempConn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing temporary connection", e);
                }
            }
        }
    }

    public static void createDatabaseIfNotExists() {
        if (!checkDatabaseExists(LIBRARY_DB)) {
            LOGGER.log(Level.FINE, "Creating database: " + LIBRARY_DB);

            Connection tempConn = null;
            try {
                String url = SERVER_URL + "postgres";
                tempConn = DriverManager.getConnection(url, USER, PASSWORD);

                String sql = "CREATE DATABASE " + LIBRARY_DB;
                try (var stmt = tempConn.createStatement()) {
                    stmt.executeUpdate(sql);
                }

                LOGGER.log(Level.FINE, "Database '" + LIBRARY_DB + "' created successfully!");

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error creating database", e);
            } finally {
                if (tempConn != null) {
                    try {
                        tempConn.close();
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error closing connection", e);
                    }
                }
            }
        }
    }

    public static void listAllDatabases() {
        Connection tempConn = null;
        try {
            String url = SERVER_URL + "postgres";
            tempConn = DriverManager.getConnection(url, USER, PASSWORD);

            try (var stmt = tempConn.createStatement();
                 var rs = stmt.executeQuery(
                         "SELECT datname FROM pg_database WHERE datistemplate = false ORDER BY datname"
                 )) {

                LOGGER.log(Level.FINE, "\nAvailable databases on server:");
                LOGGER.log(Level.FINE, "-".repeat(40));
                while (rs.next()) {
                    LOGGER.log(Level.FINE, "• " + rs.getString("datname"));
                }
                LOGGER.log(Level.FINE, "-".repeat(40));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing databases", e);
        } finally {
            if (tempConn != null) {
                try {
                    tempConn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        LOGGER.setLevel(Level.INFO);

        testConnection();
        listAllDatabases();

        if (isConnected()) {
            LOGGER.log(Level.INFO, "Database is connected");
        }

        Connection conn = switchDatabase("another_db");
        if (conn != null) {
            LOGGER.log(Level.INFO, "Successfully switched to another database");
        }

        checkDatabaseExists("test_db");

        createDatabaseIfNotExists();

        closeConnection();

        LOGGER.setLevel(Level.WARNING);
    }
}