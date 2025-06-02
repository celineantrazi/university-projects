/*package com.example.art_gallery.controllers;

import com.example.art_gallery.dbHandling;
import com.example.art_gallery.models.Artwork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    public List<Artwork> getArtworks(int offset, int limit) {
        List<Artwork> artworks = new ArrayList<>();
        String query = "SELECT id, title, description, price, upload_date, image_path, artist_id FROM artwork ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String price = rs.getString("price");
                String upload_date = rs.getString("upload_date");
                String imagePath = rs.getString("image_path");
                int artistId = rs.getInt("artist_id");
                artworks.add(new Artwork(id, title, description, price, upload_date, imagePath, artistId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artworks;
    }

    public int getArtworksCount() {
        String query = "SELECT COUNT(*) FROM artwork";
        try (Connection conn = dbHandling.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
*/