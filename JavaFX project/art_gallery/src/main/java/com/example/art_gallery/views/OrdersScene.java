package com.example.art_gallery.views;

import com.example.art_gallery.controllers.OrdersController;
import com.example.art_gallery.controllers.AuthController;
import com.example.art_gallery.models.Artwork;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrdersScene {
    private Stage stage;
    private Scene scene;
    private Artwork artwork;
    private OrdersController ordersController;

    public OrdersScene(Stage stage, Artwork artwork) {
        this.stage = stage;
        this.artwork = artwork;
        this.ordersController = new OrdersController();

        Label titleLabel = new Label("Purchasing: " + artwork.getTitle());
        TextField nameField = new TextField();
        TextField addressField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button purchaseButton = new Button("purchase");
        ImageView artworkImageView = new ImageView();
        try {
            Image artworkImage = new Image("file:" + artwork.getImagePath());
            artworkImageView.setImage(artworkImage);
            artworkImageView.setFitWidth(200);
            artworkImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
        }

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("full name"), 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(new Label("address"), 0, 1);
        gp.add(addressField, 1, 1);
        gp.add(new Label("password"), 0, 2);
        gp.add(passwordField, 1, 2);
        gp.add(purchaseButton, 1, 4);
        GridPane.setHalignment(purchaseButton, HPos.RIGHT);
        nameField.setPromptText("Enter your full name");
        addressField.setPromptText("Enter your address");
        passwordField.setPromptText("Enter your password");

        purchaseButton.setOnAction(e -> {
            String enteredPassword = passwordField.getText();
            int userId = AuthController.getLoggedInUserId();
            String currentUsername = AuthController.getUsernameByUserId(userId);
            boolean passwordCorrect = ordersController.verifyPassword(currentUsername, enteredPassword);
            if (!passwordCorrect) {
                showAlert(Alert.AlertType.ERROR, "incorrect password. can't purchase artwork.");
            } else {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "do you want to confirm the payment?", ButtonType.YES, ButtonType.NO);
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        double price = Double.parseDouble(artwork.getPrice());
                        boolean orderInserted = ordersController.insertOrder(artwork.getId(), userId, price);
                        if (orderInserted) {
                            showAlert(Alert.AlertType.INFORMATION, "purchase of \"" + artwork.getTitle() + "\" successful!");
                            stage.setScene(new HomeScene(stage, AuthController.getLoggedInUserRole()).getScene());
                        } else {
                            showAlert(Alert.AlertType.ERROR, "failed to complete purchase");
                        }
                    }
                });
            }
        });

        VBox vb = new VBox(20,artworkImageView, titleLabel, gp);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
