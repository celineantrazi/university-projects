package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UploadArtworkController {
    private Connection connection;

    public UploadArtworkController() {
        this.connection = dbHandling.getConnection();
    }

    public boolean addArtwork(String title, String description, double price, String imagePath, int artistId) {
        String sql = "INSERT INTO artwork (title, description, price, image_path, artist_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setString(4, imagePath);
            stmt.setInt(5, artistId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
