package com.app.dao;

import com.app.db.DatabaseManager;
import com.app.models.GoalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDao {
    public void insert(String name, double curr, double tgt) {
        String sql = "INSERT INTO savings_goals (goal_name, current_amount, target_amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, curr);
            pstmt.setDouble(3, tgt);
            pstmt.executeUpdate();
        } catch (SQLException ignore) {}
    }

    public List<GoalRecord> getAll() {
        List<GoalRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM savings_goals")) {
            while (rs.next()) list.add(new GoalRecord(rs.getInt("id"), rs.getString("goal_name"), rs.getDouble("current_amount"), rs.getDouble("target_amount")));
        } catch (SQLException ignore) {}
        return list;
    }

    public void addFunds(int id, double amountAdded) {
        String sql = "UPDATE savings_goals SET current_amount = current_amount + ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amountAdded);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ignore) {}
    }
}
