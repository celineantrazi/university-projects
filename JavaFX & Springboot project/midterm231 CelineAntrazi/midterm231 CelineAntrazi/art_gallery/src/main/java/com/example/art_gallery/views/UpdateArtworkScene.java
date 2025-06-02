package com.example.art_gallery.views;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateArtworkScene {
    private final Stage stage;
    private final Scene scene;

    public UpdateArtworkScene(Stage stage, int userId, String username, String role) {
        this.stage = stage;

        Label updateArtwork = new Label("Update Artwork");
        TextField titleField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        Button updateArtworkButton = new Button("Update");
        Label cancelUploadLabel = new Label("Cancel");

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("Title"), 0, 0);
        gp.add(titleField, 1, 0);
        gp.add(new Label("Description"), 0, 1);
        gp.add(descriptionField, 1, 1);
        gp.add(new Label("Price"), 0, 2);
        gp.add(priceField, 1, 2);
        gp.add(updateArtworkButton, 1, 4);
        GridPane.setHalignment(updateArtworkButton, HPos.RIGHT);

        cancelUploadLabel.setOnMouseClicked(e ->
                stage.setScene(new HomeScene(stage, userId, username, role).getScene())
        );

        updateArtworkButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                showAlert("All fields must be filled!");
                return;
            }

            double price;
            try {
                price = Math.round(Double.parseDouble(priceText) * 100.0) / 100.0;
            } catch (NumberFormatException ex) {
                showAlert("Invalid price format!");
                return;
            }


            try {
                URL url = new URL("http://localhost:8080/api/artwork/getall");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder jsonResult = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResult.append(line);
                }
                reader.close();

                JSONArray artworks = new JSONArray(jsonResult.toString());
                boolean found = false;

                for (int i = 0; i < artworks.length(); i++) {
                    JSONObject art = artworks.getJSONObject(i);
                    if (art.getString("title").equalsIgnoreCase(title)) {
                        JSONObject userJson = art.getJSONObject("user");
                        String ownerUsername = userJson.getString("username");
                        int artworkId = art.getInt("id");

                        if (ownerUsername.equals(username)) {
                            found = true;

                            Alert confirmDelete = new Alert(
                                    Alert.AlertType.CONFIRMATION,
                                    "Are you sure you want to update this artwork?",
                                    ButtonType.OK, ButtonType.CANCEL
                            );

                            confirmDelete.showAndWait().ifPresent(resp -> {
                                if (resp == ButtonType.OK) {
                                    try {
                                        URL updateURL = new URL("http://localhost:8080/api/artwork/update/" + artworkId + "?userId=" + userId);
                                        HttpURLConnection updateConn = (HttpURLConnection) updateURL.openConnection();
                                        updateConn.setRequestMethod("PUT");
                                        updateConn.setRequestProperty("Content-Type", "application/json");
                                        updateConn.setDoOutput(true);

                                        JSONObject json = new JSONObject();
                                        json.put("title", title);
                                        json.put("description", description);
                                        json.put("price", price);

                                        try (OutputStream os = updateConn.getOutputStream()) {
                                            os.write(json.toString().getBytes());
                                        }

                                        int status = updateConn.getResponseCode();

                                        if (status == 200) {
                                            showAlert("Artwork updated successfully!");
                                            stage.setScene(new HomeScene(
                                                    stage, userId, username, role
                                            ).getScene());
                                        } else {
                                            showAlert("Failed to update artwork.");
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        showAlert("Error while updating artwork.");
                                    }
                                }
                            });
                            break;
                        }
                    }
                }

                if (!found) {
                    showAlert("You don't own this artwork or it doesn't exist.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Could not connect to server.");
            }
        });

        VBox vb = new VBox(20, updateArtwork, gp, cancelUploadLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Update Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
