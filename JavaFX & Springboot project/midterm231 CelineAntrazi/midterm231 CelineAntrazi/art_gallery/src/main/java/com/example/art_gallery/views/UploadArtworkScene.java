package com.example.art_gallery.views;

import com.example.art_gallery.models.Artwork;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadArtworkScene {
    private final Stage stage;
    private final Scene scene;

    public UploadArtworkScene(Stage stage, int userId, String username, String role) {
        this.stage = stage;

        Label addArtwork = new Label("Add Artwork");
        TextField titleField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField imagePathField = new TextField();
        Button uploadArtworkButton = new Button("Add");
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
        gp.add(new Label("Image Path"), 0, 3);
        gp.add(imagePathField, 1, 3);
        gp.add(uploadArtworkButton, 1, 4);
        GridPane.setHalignment(uploadArtworkButton, HPos.RIGHT);

        cancelUploadLabel.setOnMouseClicked(e ->
                stage.setScene(new HomeScene(stage, userId, username, role).getScene())
        );

        uploadArtworkButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();
            String imagePath = imagePathField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || priceText.isEmpty() || imagePath.isEmpty()) {
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
                URL url = new URL("http://localhost:8080/api/artwork/create?userId=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("title", title);
                payload.put("description", description);
                payload.put("imagePath", imagePath);
                payload.put("price", price);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes());
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    stage.setScene(new HomeScene(stage, userId, username, role).getScene());
                } else {
                    showAlert("Failed to upload artwork. Response code: " + responseCode);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Could not connect to server.");
            }
        });

        VBox vb = new VBox(20, addArtwork, gp, cancelUploadLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Upload Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
