package com.app.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

public class DataSeeder {

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        seedData();
    }

    public static void seedData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Seeding database with sample data...");
            
            // Clear existing data to avoid duplicates/confusion if needed, 
            // but usually seeder adds to existing or starts fresh. 
            // For testing, let's just add new ones.
            
            seedHealthRecords(conn);
            seedFinanceRecords(conn);
            seedBudgets(conn);
            seedSavingsGoals(conn);
            
            System.out.println("Database seeding completed successfully!");
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }

    private static void seedHealthRecords(Connection conn) throws SQLException {
        String sql = "INSERT INTO health_records (date, weight, blood_pressure, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            Random rand = new Random();
            for (int i = 0; i < 15; i++) {
                pstmt.setString(1, today.minusDays(i * 2).toString());
                pstmt.setDouble(2, 70.0 + rand.nextDouble() * 5.0);
                pstmt.setString(3, (110 + rand.nextInt(20)) + "/" + (70 + rand.nextInt(15)));
                pstmt.setString(4, "Registro de salud de prueba #" + i);
                pstmt.executeUpdate();
            }
        }
    }

    private static void seedFinanceRecords(Connection conn) throws SQLException {
        String sql = "INSERT INTO finance_records (date, type, amount, category, description, account, source_tag, payment_method, is_recurring) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] categories = {"Comida", "Transporte", "Vivienda", "Ocio", "Salud", "Sueldo", "Freelance"};
        String[] accounts = {"Efectivo", "Banco Principal", "Ahorros"};
        String[] paymentMethods = {"Efectivo", "Tarjeta de Crédito", "Transferencia"};
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            Random rand = new Random();
            
            // Add some income
            for (int i = 0; i < 5; i++) {
                pstmt.setString(1, today.minusDays(i * 10).toString());
                pstmt.setString(2, "Ingreso");
                pstmt.setDouble(3, 1000.0 + rand.nextDouble() * 2000.0);
                pstmt.setString(4, i % 2 == 0 ? "Sueldo" : "Freelance");
                pstmt.setString(5, "Ingreso mensual #" + i);
                pstmt.setString(6, "Banco Principal");
                pstmt.setString(7, "Trabajo");
                pstmt.setString(8, "Transferencia");
                pstmt.setBoolean(9, true);
                pstmt.executeUpdate();
            }

            // Add many expenses
            for (int i = 0; i < 30; i++) {
                pstmt.setString(1, today.minusDays(rand.nextInt(60)).toString());
                pstmt.setString(2, "Gasto");
                pstmt.setDouble(3, 10.0 + rand.nextDouble() * 150.0);
                pstmt.setString(4, categories[rand.nextInt(5)]); // Only the first 5 are expense categories
                pstmt.setString(5, "Gasto de prueba #" + i);
                pstmt.setString(6, accounts[rand.nextInt(accounts.length)]);
                pstmt.setString(7, "Personal");
                pstmt.setString(8, paymentMethods[rand.nextInt(paymentMethods.length)]);
                pstmt.setBoolean(9, false);
                pstmt.executeUpdate();
            }
        }
    }

    private static void seedBudgets(Connection conn) throws SQLException {
        String sql = "INSERT OR IGNORE INTO budgets (category, limit_amount) VALUES (?, ?)";
        Object[][] budgets = {
            {"Comida", 500.0},
            {"Transporte", 200.0},
            {"Vivienda", 1200.0},
            {"Ocio", 300.0},
            {"Salud", 150.0}
        };
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] budget : budgets) {
                pstmt.setString(1, (String) budget[0]);
                pstmt.setDouble(2, (Double) budget[1]);
                pstmt.executeUpdate();
            }
        }
    }

    private static void seedSavingsGoals(Connection conn) throws SQLException {
        String sql = "INSERT INTO savings_goals (goal_name, current_amount, target_amount) VALUES (?, ?, ?)";
        Object[][] goals = {
            {"Viaje a Japón", 1500.0, 5000.0},
            {"Nuevo PC", 800.0, 1500.0},
            {"Fondo de Emergencia", 2000.0, 10000.0}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] goal : goals) {
                pstmt.setString(1, (String) goal[0]);
                pstmt.setDouble(2, (Double) goal[1]);
                pstmt.setDouble(3, (Double) goal[2]);
                pstmt.executeUpdate();
            }
        }
    }
}
