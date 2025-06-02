package com.example.art_gallery.views;

import com.example.art_gallery.controllers.AuthController;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupScene {
    private Stage stage;
    private AuthController authController;
    private Scene scene;

    public SignupScene(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;

        Label welcome = new Label("Welcome!");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("artist", "user");
        roleBox.setPromptText("select role");
        Button signupButton = new Button("complete");
        Label loginLabel = new Label("Already have an account? Login!");
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("username"), 0, 0);
        gp.add(usernameField, 1, 0);
        gp.add(new Label("password"), 0, 1);
        gp.add(passwordField, 1, 1);
        gp.add(new Label("role"), 0, 2);
        gp.add(roleBox, 1, 2);
        gp.add(signupButton, 1, 3);
        GridPane.setHalignment(signupButton, HPos.RIGHT);
        loginLabel.setOnMouseClicked(e -> stage.setScene(new LoginScene(stage, authController).getScene()));
        signupButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleBox.getValue();
            if (username.isEmpty() || password.isEmpty() || role == null) {
                showAlert("ALL FIELDS MUST BE FILLED!");
            } else if (authController.signup(username, password, role)) {
                stage.setScene(new HomeScene(stage, role).getScene());
            }
            else
                showAlert("username already exists");
        });

        VBox vb = new VBox(20, welcome, gp, loginLabel);
        vb.setAlignment(Pos.CENTER);
        scene = new Scene(vb, 800, 600);
        vb.setStyle("-fx-background-color: #c5d6ec;");
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
