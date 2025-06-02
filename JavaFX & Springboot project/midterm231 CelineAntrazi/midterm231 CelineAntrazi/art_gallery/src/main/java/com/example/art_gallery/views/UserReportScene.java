package com.example.art_gallery.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserReportScene {
    private final Scene scene;

    public UserReportScene(Stage stage, int userId, String username, String role) {
        Label title = new Label("Your Report");
        Label spentLabel = new Label();
        Label earnedLabel = new Label();
        Button backButton = new Button("Back");

        backButton.setOnAction(e ->
                stage.setScene(new HomeScene(stage, userId, username, role).getScene())
        );

        VBox vbox = new VBox(15, title, spentLabel, earnedLabel, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #c5d6ec;");
        scene = new Scene(vbox, 800, 600);

        try {
            URL url = new URL("http://localhost:8080/api/user/get/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
                reader.close();

                JSONObject userJson = new JSONObject(json.toString());

                double spent = userJson.getDouble("totalSpent");
                spentLabel.setText("Total Spent: $" + spent);

                if (role.equals("artist")) {
                    double earned = userJson.getDouble("totalIncome");
                    earnedLabel.setText("Total Earned: $" + earned);
                } else {
                    earnedLabel.setText("");
                }
            } else {
                spentLabel.setText("Failed to load report.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            spentLabel.setText("Error connecting to server.");
        }
    }

    public Scene getScene() {
        return scene;
    }
}
