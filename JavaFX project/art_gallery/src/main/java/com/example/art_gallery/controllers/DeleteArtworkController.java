package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteArtworkController {
    public boolean isArtworkOwnedByArtist(String title, int artistId) {
        String query = "SELECT artist_id FROM artwork WHERE title = ?";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int artworkArtistId = rs.getInt("artist_id");
                return artworkArtistId == artistId;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteArtwork(String title, int artistId) {
        String query = "DELETE FROM artwork WHERE title = ? AND artist_id = ?";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setInt(2, artistId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
