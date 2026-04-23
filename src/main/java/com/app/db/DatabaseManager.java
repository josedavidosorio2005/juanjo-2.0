package com.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.MessageDigest;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Core Tables
            stmt.execute("CREATE TABLE IF NOT EXISTS health_records (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, weight REAL, blood_pressure TEXT, notes TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS finance_records (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, type TEXT NOT NULL, amount REAL NOT NULL, category TEXT, description TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS budgets (id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT UNIQUE, limit_amount REAL NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS savings_goals (id INTEGER PRIMARY KEY AUTOINCREMENT, goal_name TEXT NOT NULL, current_amount REAL NOT NULL, target_amount REAL NOT NULL);");
            
            // New Enterprise Table (Security Phase)
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password_hash TEXT);");

            // Insert default Admin user if empty
            try {
                // SHA-256 hash of 'admin'
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest("admin".getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String defaultHash = hexString.toString();
                stmt.execute("INSERT INTO users (username, password_hash) SELECT 'admin', '" + defaultHash + "' WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');");
            } catch (Exception ignore) {}

            // Migrations for Advanced Finance Record
            String[] migrations = {
                "ALTER TABLE finance_records ADD COLUMN account TEXT;",
                "ALTER TABLE finance_records ADD COLUMN source_tag TEXT;",
                "ALTER TABLE finance_records ADD COLUMN payment_method TEXT;",
                "ALTER TABLE finance_records ADD COLUMN is_recurring BOOLEAN;"
            };
            
            for (String migration : migrations) {
                try {
                    stmt.execute(migration);
                } catch (SQLException ignore) {}
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
