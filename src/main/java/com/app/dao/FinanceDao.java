package com.app.dao;

import com.app.db.DatabaseManager;
import com.app.models.FinanceRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceDao {

    public void insertRecord(FinanceRecord record) {
        String sql = "INSERT INTO finance_records (date, type, amount, category, description, account, source_tag, payment_method, is_recurring) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, record.getDate());
            pstmt.setString(2, record.getType());
            pstmt.setDouble(3, record.getAmount());
            pstmt.setString(4, record.getCategory());
            pstmt.setString(5, record.getDescription());
            pstmt.setString(6, record.getAccount());
            pstmt.setString(7, record.getSourceTag());
            pstmt.setString(8, record.getPaymentMethod());
            pstmt.setBoolean(9, record.isRecurring());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error inserting finance record: " + e.getMessage());
        }
    }

    public List<FinanceRecord> getAllRecords() {
        List<FinanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM finance_records ORDER BY id DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                FinanceRecord record = new FinanceRecord(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("account"),
                        rs.getString("source_tag"),
                        rs.getString("payment_method"),
                        rs.getBoolean("is_recurring")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching finance records: " + e.getMessage());
        }
        return records;
    }
}
