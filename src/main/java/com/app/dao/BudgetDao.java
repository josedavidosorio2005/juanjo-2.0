package com.app.dao;

import com.app.db.DatabaseManager;
import com.app.models.BudgetRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDao {
    public void upsertBudget(String category, double limit) {
        String sql = "INSERT INTO budgets (category, limit_amount) VALUES (?, ?) ON CONFLICT(category) DO UPDATE SET limit_amount = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, limit);
            pstmt.setDouble(3, limit);
            pstmt.executeUpdate();
        } catch (SQLException ignore) {}
    }

    public List<BudgetRecord> getAll() {
        List<BudgetRecord> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM budgets")) {
            while (rs.next()) list.add(new BudgetRecord(rs.getInt("id"), rs.getString("category"), rs.getDouble("limit_amount")));
        } catch (SQLException ignore) {}
        return list;
    }
}
