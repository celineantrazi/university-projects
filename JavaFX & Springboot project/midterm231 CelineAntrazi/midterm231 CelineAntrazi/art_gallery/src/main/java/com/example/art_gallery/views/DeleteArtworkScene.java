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
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteArtworkScene {
    private final Stage stage;
    private final Scene scene;

    public DeleteArtworkScene(Stage stage, int userId, String username, String role) {
        this.stage = stage;

        Label deleteArtwork = new Label("Delete Artwork");
        TextField titleField = new TextField();
        Button deleteArtworkButton = new Button("Delete");
        Label cancelDeleteLabel = new Label("Cancel");

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("Title"), 0, 0);
        gp.add(titleField, 1, 0);
        gp.add(deleteArtworkButton, 1, 1);
        GridPane.setHalignment(deleteArtworkButton, HPos.RIGHT);

        cancelDeleteLabel.setOnMouseClicked(e ->
                stage.setScene(new HomeScene(stage, userId, username, role).getScene())
        );

        deleteArtworkButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                showAlert("PROVIDE A TITLE!");
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
                                    "Are you sure you want to delete this artwork?",
                                    ButtonType.OK, ButtonType.CANCEL
                            );
                            confirmDelete.setTitle("Confirm Deletion");
                            confirmDelete.setHeaderText("This action can't be reversed.");
                            confirmDelete.showAndWait().ifPresent(resp -> {
                                if (resp == ButtonType.OK) {
                                    try {
                                        URL deleteUrl = new URL(
                                                "http://localhost:8080/api/artwork/delete/" + artworkId
                                        );
                                        HttpURLConnection deleteConn = (HttpURLConnection)
                                                deleteUrl.openConnection();
                                        deleteConn.setRequestMethod("GET");
                                        int status = deleteConn.getResponseCode();
                                        if (status == 200) {
                                            showAlert("Artwork deleted successfully!");
                                            stage.setScene(new HomeScene(
                                                    stage, userId, username, role
                                            ).getScene());
                                        } else {
                                            showAlert("Failed to delete artwork.");
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        showAlert("Error while deleting artwork.");
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

        VBox vb = new VBox(20, deleteArtwork, gp, cancelDeleteLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delete Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
