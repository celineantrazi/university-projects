package com.example.art_gallery.views;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginScene {
    private final Stage stage;
    private final Scene scene;

    public LoginScene(Stage stage) {
        this.stage = stage;

        Label welcomeBack = new Label("Welcome Back!");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label signupLabel = new Label("Don't have an account? Sign up!");

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("Username"), 0, 0);
        gp.add(usernameField, 1, 0);
        gp.add(new Label("Password"), 0, 1);
        gp.add(passwordField, 1, 1);
        gp.add(loginButton, 1, 2);
        GridPane.setHalignment(loginButton, HPos.RIGHT);

        signupLabel.setOnMouseClicked(e ->
                stage.setScene(new SignupScene(stage).getScene())
        );

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("USERNAME AND PASSWORD CAN'T BE EMPTY!");
                return;
            }

            try {
                String loginUrl = String.format(
                        "http://localhost:8080/api/user/login?username=%s&password=%s",
                        URLEncoder.encode(username, StandardCharsets.UTF_8),
                        URLEncoder.encode(password, StandardCharsets.UTF_8)
                );
                HttpURLConnection conn = (HttpURLConnection) new URL(loginUrl).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    showAlert("Username doesn't exist or password is incorrect.");
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject userJson = new JSONObject(sb.toString());
                int userId = userJson.getInt("id");
                String role = userJson.getString("role");

                stage.setScene(new HomeScene(stage, userId, username, role).getScene());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error connecting to server.");
            }
        });

        VBox vb = new VBox(20, welcomeBack, gp, signupLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
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
