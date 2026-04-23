package com.app.dao;

import com.app.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

public class UserDao {

    public boolean validateLogin(String username, String rawPassword) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Hash password to match DB
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String attemptHash = hexString.toString();

            pstmt.setString(1, username);
            pstmt.setString(2, attemptHash);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True if match found
            
        } catch (Exception e) {}
        return false;
    }
}
