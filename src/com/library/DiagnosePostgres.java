package com.library;

import java.sql.*;

public class DiagnosePostgres {

    public static void main(String[] args) {
        System.out.println("\nPOSTGRESQL CONNECTION DIAGNOSTIC TOOL");
        System.out.println("=".repeat(60));

        checkDriver();
        listDatabases();
        testAllDatabases();
        checkPostgresSettings();
    }

    private static void checkDriver() {
        System.out.println("\nCHECKING POSTGRESQL DRIVER");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver found");

            Driver driver = DriverManager.getDriver("jdbc:postgresql://localhost:2038/test");
            System.out.println("   Driver: " + driver.getClass().getName());
            System.out.println("   Version: " + driver.getMajorVersion() + "." + driver.getMinorVersion());

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver NOT FOUND!");
            System.err.println("   Please add PostgreSQL JDBC driver to your classpath");
            System.err.println("   Download: https://jdbc.postgresql.org/download.html");
        } catch (SQLException e) {
            System.out.println("Driver found, but connection test failed (expected)");
        }
    }

    private static void listDatabases() {
        System.out.println("\nLISTING AVAILABLE DATABASES");

        String url = "jdbc:postgresql://localhost:2038/postgres";
        String user = "postgres";
        String password = "dayana1809";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT datname, pg_size_pretty(pg_database_size(datname)) as size " +
                            "FROM pg_database WHERE datistemplate = false ORDER BY datname"
            );

            System.out.println("Databases on localhost:2038:");
            System.out.println("-".repeat(50));
            while (rs.next()) {
                System.out.printf("• %-20s (%s)%n",
                        rs.getString("datname"),
                        rs.getString("size"));
            }
            System.out.println("-".repeat(50));

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println(" Cannot list databases: " + e.getMessage());
            System.err.println("   Make sure PostgreSQL is running and credentials are correct");
        }
    }

    private static void testAllDatabases() {
        System.out.println("\nTESTING CONNECTIONS TO ALL DATABASES");

        String[] databases = {"postgres", "library_db", "Assignment 1"};
        String user = "postgres";
        String password = "dayana1809";

        for (String db : databases) {
            System.out.printf("\nTesting '%s'... ", db);

            String url = "jdbc:postgresql://localhost:2038/" + db;

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("CONNECTED");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT version(), current_database(), current_user"
                );

                if (rs.next()) {
                    System.out.println("   Database: " + rs.getString(2));
                    System.out.println("   User: " + rs.getString(3));
                }

                rs.close();
                stmt.close();

            } catch (SQLException e) {
                System.out.println("FAILED: " + e.getMessage());
            }
        }
    }

    private static void checkPostgresSettings() {
        System.out.println("\nCHECKING POSTGRESQL SETTINGS");

        String url = "jdbc:postgresql://localhost:2038/postgres";
        String user = "postgres";
        String password = "dayana1809";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();

            String[] settings = {
                    "tcp_keepalives_idle",
                    "tcp_keepalives_interval",
                    "tcp_keepalives_count",
                    "connect_timeout",
                    "listen_addresses",
                    "max_connections",
                    "statement_timeout",
                    "idle_in_transaction_session_timeout"
            };

            System.out.println("PostgreSQL Settings:");
            System.out.println("-".repeat(50));

            for (String setting : settings) {
                ResultSet rs = stmt.executeQuery(
                        "SELECT name, setting, unit FROM pg_settings WHERE name = '" + setting + "'"
                );

                if (rs.next()) {
                    System.out.printf("%-35s: %s %s%n",
                            rs.getString("name"),
                            rs.getString("setting"),
                            rs.getString("unit") != null ? rs.getString("unit") : ""
                    );
                }
                rs.close();
            }

            System.out.println("-".repeat(50));

            stmt.close();

        } catch (SQLException e) {
            System.err.println("Cannot check settings: " + e.getMessage());
        }
    }
}