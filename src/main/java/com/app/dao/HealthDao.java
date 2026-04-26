package com.app.dao;

import com.app.db.DatabaseManager;
import com.app.models.HealthRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HealthDao {

    public boolean insertRecord(HealthRecord record) {
        String sql = "INSERT INTO health_records (date, weight, blood_pressure, notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, record.getDate());
            pstmt.setDouble(2, record.getWeight());
            pstmt.setString(3, record.getBloodPressure());
            pstmt.setString(4, record.getNotes());
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error inserting health record: " + e.getMessage());
            return false;
        }
    }

    public List<HealthRecord> getAllRecords() {
        List<HealthRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM health_records ORDER BY id DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HealthRecord record = new HealthRecord(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getDouble("weight"),
                        rs.getString("blood_pressure"),
                        rs.getString("notes")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching health records: " + e.getMessage());
        }
        return records;
    }
}
