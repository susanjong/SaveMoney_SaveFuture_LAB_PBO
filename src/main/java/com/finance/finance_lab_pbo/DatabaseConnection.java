package com.finance.finance_lab_pbo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Neon PostgreSQL connection details (URL decoded)
    private static final String URL = "jdbc:postgresql://ep-floral-star-a1jgsqi0-pooler.ap-southeast-1.aws.neon.tech/Save Money to Save Future?sslmode=require";
    private static final String USER = "Save Money to Save Future_owner";
    private static final String PASSWORD = "npg_5ozvCDkriaV0";
    
    // Get a database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load PostgreSQL driver explicitly
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
}