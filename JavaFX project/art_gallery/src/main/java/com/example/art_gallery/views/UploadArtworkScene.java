package com.example.art_gallery.views;

import com.example.art_gallery.controllers.UploadArtworkController;
import com.example.art_gallery.controllers.AuthController;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UploadArtworkScene {
    private Stage stage;
    private UploadArtworkController uploadArtworkController;
    private Scene scene;
    public UploadArtworkScene(Stage stage, UploadArtworkController uploadArtworkController) {
        this.stage = stage;
        this.uploadArtworkController = uploadArtworkController;

        Label addArtwork = new Label("add artwork");
        TextField titleField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField imagePathField = new TextField();
        Button uploadArtworkButton = new Button("add");
        Label cancelUploadLabel = new Label("cancel");
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("title"), 0, 0);
        gp.add(titleField, 1, 0);
        gp.add(new Label("description"), 0, 1);
        gp.add(descriptionField, 1, 1);
        gp.add(new Label("price"), 0, 2);
        gp.add(priceField, 1, 2);
        gp.add(new Label("image path"), 0, 3);
        gp.add(imagePathField, 1, 3);
        gp.add(uploadArtworkButton, 1, 4);
        GridPane.setHalignment(uploadArtworkButton, HPos.RIGHT);
        cancelUploadLabel.setOnMouseClicked(e -> stage.setScene(new HomeScene(stage, "artist").getScene()));
        uploadArtworkButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String priceT = priceField.getText();
            double price = Double.parseDouble(priceT);
            try {
                price = Math.round(price * 100.0) / 100.0;
            } catch (NumberFormatException t) {
                showAlert("INVALID PRICE FORMAT!");
            }
            String imagePath = imagePathField.getText();

            if (title.isEmpty() || description.isEmpty() || priceT.isEmpty() || imagePath.isEmpty()) {
                showAlert("ALL FIELDS MUST BE FILLED!");
            } else {
                int artistId = AuthController.getLoggedInUserId();
                if (uploadArtworkController.addArtwork(title, description, price, imagePath, artistId))
                    stage.setScene(new HomeScene(stage, "artist").getScene());
                else
                    showAlert("can't add artwork");
            }
        });

        VBox vb = new VBox(20, addArtwork, gp, cancelUploadLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}