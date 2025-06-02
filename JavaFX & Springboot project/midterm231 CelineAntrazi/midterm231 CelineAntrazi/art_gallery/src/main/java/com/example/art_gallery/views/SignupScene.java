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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SignupScene {
    private final Stage stage;
    private final Scene scene;

    public SignupScene(Stage stage) {
        this.stage = stage;

        Label welcome = new Label("Welcome!");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("artist", "user");
        roleBox.setPromptText("Select Role");

        Button signupButton = new Button("Complete");
        Label loginLabel = new Label("Already have an account? Login!");

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(15);
        gp.setHgap(15);
        gp.add(new Label("Username"), 0, 0);
        gp.add(usernameField, 1, 0);
        gp.add(new Label("Password"), 0, 1);
        gp.add(passwordField, 1, 1);
        gp.add(new Label("Role"), 0, 2);
        gp.add(roleBox, 1, 2);
        gp.add(signupButton, 1, 3);
        GridPane.setHalignment(signupButton, HPos.RIGHT);

        loginLabel.setOnMouseClicked(e ->
                stage.setScene(new LoginScene(stage).getScene())
        );

        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                showAlert("All fields must be filled!");
                return;
            }

            try {
                URL signupUrl = new URL("http://localhost:8080/api/user/signup");
                HttpURLConnection conn = (HttpURLConnection) signupUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                String payload = String.format(
                        "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                        URLEncoder.encode(username, StandardCharsets.UTF_8),
                        URLEncoder.encode(password, StandardCharsets.UTF_8),
                        role
                );
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 201) {
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

                    stage.setScene(new HomeScene(stage, userId, username, role).getScene());
                } else if (code == 400) {
                    showAlert("Username already exists.");
                } else {
                    showAlert("Signup failed: " + code);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Could not connect to server.");
            }
        });

        VBox vb = new VBox(20, welcome, gp, loginLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vb, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Signup Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
