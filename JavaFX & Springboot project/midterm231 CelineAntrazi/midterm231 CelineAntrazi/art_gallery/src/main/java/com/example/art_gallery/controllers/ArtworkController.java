/*package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtworkController {
    public boolean addArtwork(String title, String description, String imageUrl, double price, String artistUsername) {
        String getArtistIdQuery = "SELECT id FROM users WHERE username = ?";
        String insertArtworkQuery = "INSERT INTO artwork (title, description, price, image_path, artist_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement getArtistStmt = conn.prepareStatement(getArtistIdQuery)) {
            getArtistStmt.setString(1, artistUsername);
            ResultSet rs = getArtistStmt.executeQuery();
            if (rs.next()) {
                int artistId = rs.getInt("id");
                try (PreparedStatement insertArtworkStmt = conn.prepareStatement(insertArtworkQuery)) {
                    insertArtworkStmt.setString(1, title);
                    insertArtworkStmt.setString(2, description);
                    insertArtworkStmt.setDouble(3, price);
                    insertArtworkStmt.setString(4, imageUrl);
                    insertArtworkStmt.setInt(5, artistId);

                    insertArtworkStmt.executeUpdate();
                    return true;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
*/