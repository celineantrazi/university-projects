package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;
import com.example.art_gallery.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthController {
    private static User loggedInUser;
    private static int loggedInUserId = -1;

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static String getLoggedInUserRole() {
        if (loggedInUser != null) {
            return loggedInUser.getRole();
        }
        return "guest";
    }

    public static String getUsernameByUserId(int userId) {
        String query = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "unknown artist";
    }

    public boolean login(String username, String password) {
        String query = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id");
                String role = rs.getString("role");
                loggedInUser = new User(loggedInUserId,username, role);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean signup(String username, String password, String role) {
        String checkQuery = "SELECT * FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, role);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
