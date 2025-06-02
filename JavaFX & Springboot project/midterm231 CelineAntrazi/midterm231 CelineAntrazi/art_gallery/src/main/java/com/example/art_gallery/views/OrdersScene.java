package com.example.art_gallery.views;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrdersScene {
    private final Stage stage;
    private final Scene scene;
    private final Artwork artwork;
    private final int userId;
    private final String username;
    private final String userRole;

    public OrdersScene(Stage stage, Artwork artwork, int userId, String username, String userRole) {
        this.stage = stage;
        this.artwork = artwork;
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;

        Label titleLabel = new Label("Purchasing: " + artwork.getTitle());
        TextField nameField = new TextField();
        TextField addressField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button purchaseButton = new Button("Purchase");

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
        gp.add(new Label("Full Name"), 0, 0);
        gp.add(nameField, 1, 0);
        gp.add(new Label("Address"), 0, 1);
        gp.add(addressField, 1, 1);
        gp.add(new Label("Password"), 0, 2);
        gp.add(passwordField, 1, 2);
        gp.add(purchaseButton, 1, 4);
        GridPane.setHalignment(purchaseButton, HPos.RIGHT);

        nameField.setPromptText("Enter your full name");
        addressField.setPromptText("Enter your address");
        passwordField.setPromptText("Enter your password");

        purchaseButton.setOnAction(e -> {
            String enteredPassword = passwordField.getText().trim();
            if (enteredPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Password is required to confirm purchase.");
                return;
            }

            try {
                String loginUrl = "http://localhost:8080/api/user/login?username=" + username + "&password=" + enteredPassword;
                HttpURLConnection loginConn = (HttpURLConnection) new URL(loginUrl).openConnection();
                loginConn.setRequestMethod("GET");
                if (loginConn.getResponseCode() != 200) {
                    showAlert(Alert.AlertType.ERROR, "Incorrect password. Can't complete purchase.");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Do you want to confirm the payment?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        try {
                            String purchaseUrl = "http://localhost:8080/api/user/purchase?userId="
                                    + userId + "&artId=" + artwork.getId();
                            HttpURLConnection purchaseConn = (HttpURLConnection) new URL(purchaseUrl).openConnection();
                            purchaseConn.setRequestMethod("POST");
                            int status = purchaseConn.getResponseCode();

                            if (status == 200) {
                                showAlert(Alert.AlertType.INFORMATION,
                                        "Purchase of \"" + artwork.getTitle() + "\" successful!");
                                stage.setScene(new HomeScene(stage, userId, username, userRole).getScene());
                            } else if (status == 409) {
                                showAlert(Alert.AlertType.ERROR,
                                        "You can't buy your own artwork.");
                                stage.setScene(new HomeScene(stage, userId, username, userRole).getScene());

                            } else {
                                showAlert(Alert.AlertType.ERROR,
                                        "Failed to complete purchase (code " + status + ").");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error during purchase request.");
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error verifying password.");
            }
        });

        VBox vb = new VBox(20, artworkImageView, titleLabel, gp);
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
