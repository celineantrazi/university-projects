package com.example.art_gallery.views;

import com.example.art_gallery.controllers.DeleteArtworkController;
import com.example.art_gallery.controllers.AuthController;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeleteArtworkScene {
    private Stage stage;
    private DeleteArtworkController deleteArtworkController;
    private Scene scene;

    public DeleteArtworkScene(Stage stage, DeleteArtworkController deleteArtworkController) {
        this.stage = stage;
        this.deleteArtworkController = deleteArtworkController;

        Label deleteArtwork = new Label("delete artwork");
        TextField titleField = new TextField();
        Button deleteArtworkButton = new Button("delete");
        Label cancelDeleteLabel = new Label("cancel");

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);

        gp.add(new Label("title"), 0, 0);
        gp.add(titleField, 1, 0);
        gp.add(deleteArtworkButton, 1, 1);
        GridPane.setHalignment(deleteArtworkButton, HPos.RIGHT);

        cancelDeleteLabel.setOnMouseClicked(e -> stage.setScene(new HomeScene(stage, "artist").getScene()));

        deleteArtworkButton.setOnAction(e -> {
            String title = titleField.getText();

            if (title.isEmpty())
                showAlert("PROVIDE A TITLE!");
            else {
                int artistId = AuthController.getLoggedInUserId();
                if (deleteArtworkController.isArtworkOwnedByArtist(title, artistId)) {
                    Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDelete.setTitle("Confirm Deletion");
                    confirmDelete.setHeaderText("are you sure you want to delete this artwork?");
                    confirmDelete.setContentText("this action can't be reversed");
                    confirmDelete.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (deleteArtworkController.deleteArtwork(title, artistId)) {
                                showAlert("artwork deleted successfully!");
                                stage.setScene(new HomeScene(stage, "artist").getScene());
                            } else {
                                showAlert("failed to delete artwork");
                            }
                        }
                    });
                } else {
                    showAlert("you don't own this artwork or it doesn't exist");
                }
            }
        });

        VBox vb = new VBox(20, deleteArtwork, gp, cancelDeleteLabel);
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
