package com.example.art_gallery.views;

import com.example.art_gallery.controllers.AuthController;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScene {
    private Stage stage;
    private AuthController authController;
    private Scene scene;

    public LoginScene(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
        Label welcomeBack = new Label("Welcome Back!");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("complete");
        Label signupLabel = new Label("Don't have an account? Sign up!");
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);

        gp.add(new Label("username"), 0, 0);
        gp.add(usernameField, 1, 0);
        gp.add(new Label("password"), 0, 1);
        gp.add(passwordField, 1, 1);
        gp.add(loginButton, 1, 2);
        GridPane.setHalignment(loginButton, HPos.RIGHT);

        signupLabel.setOnMouseClicked(e -> stage.setScene(new SignupScene(stage, authController).getScene()));
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("USERNAME AND PASSWORD CAN'T BE EMPTY!");
            } else if (authController.login(username, password)) {
                stage.setScene(new HomeScene(stage, authController.getLoggedInUserRole()).getScene());
            }
            else
                showAlert("username doesn't exist or password is incorrect");
        });

        VBox vb = new VBox(20, welcomeBack, gp, signupLabel);
        vb.setAlignment(Pos.CENTER);
        scene = new Scene(vb, 800, 600);
        vb.setStyle("-fx-background-color: #c5d6ec;");
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
