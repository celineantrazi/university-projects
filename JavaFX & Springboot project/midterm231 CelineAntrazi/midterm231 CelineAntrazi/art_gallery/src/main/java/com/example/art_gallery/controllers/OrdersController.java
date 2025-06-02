/*package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrdersController {

    public boolean verifyPassword(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertOrder(int artworkId, int userId, double amount) {
        String insertQuery = "INSERT INTO orders (artwork_id, user_id, amount, status) VALUES (?, ?, ?, 'completed')";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, artworkId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, amount);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
*/