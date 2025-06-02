package com.example.art_gallery.views;

import com.example.art_gallery.models.Artwork;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeScene {
    private final Stage stage;
    private final Scene scene;
    private final String username;
    private final String userRole;
    private final int userId;
    private final VBox artworkContainer;
    private int offset = 0;
    private static final int LIMIT = 5;
    private final Button prevButton;
    private final Button nextButton;
    private List<Artwork> allArtworks;

    public HomeScene(Stage stage, String username, String userRole) {
        this(stage, 0, username, userRole);
    }

    public HomeScene(Stage stage, int userId, String username, String userRole) {
        this.stage = stage;
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;

        Label welcomeLabel = new Label("Welcome to the Art Gallery!");
        welcomeLabel.setFont(new Font(20));

        MenuBar menuBar = new MenuBar();
        if (userRole.equals("artist")) {
            Menu artistMenu = new Menu("Artist Actions");
            MenuItem addArtwork = new MenuItem("Add Artwork");
            MenuItem deleteArtwork = new MenuItem("Delete Artwork");
            MenuItem updateArtwork = new MenuItem("Update Artwork");
            MenuItem viewReport = new MenuItem("View Report");

            addArtwork.setOnAction(e ->
                    stage.setScene(new UploadArtworkScene(stage, userId, username, userRole).getScene())
            );
            deleteArtwork.setOnAction(e ->
                    stage.setScene(new DeleteArtworkScene(stage, userId, username, userRole).getScene())
            );
            updateArtwork.setOnAction(e ->
                    stage.setScene(new UpdateArtworkScene(stage, userId, username, userRole).getScene())
            );
            viewReport.setOnAction(e ->
                    stage.setScene(new UserReportScene(stage, userId, username, userRole).getScene())
            );

            artistMenu.getItems().addAll(addArtwork, deleteArtwork, updateArtwork, viewReport);
            menuBar.getMenus().add(artistMenu);
        } else {
            Menu userMenu = new Menu("User Actions");
            MenuItem viewReport = new MenuItem("View Report");

            viewReport.setOnAction(e ->
                    stage.setScene(new UserReportScene(stage, userId, username, userRole).getScene())
            );

            userMenu.getItems().add(viewReport);
            menuBar.getMenus().add(userMenu);
        }

        artworkContainer = new VBox(10);
        artworkContainer.setAlignment(Pos.CENTER);

        prevButton = new Button("Previous");
        nextButton = new Button("Next");

        prevButton.setOnAction(e -> {
            if (offset > 0) {
                offset -= LIMIT;
                displayArtworks();
            }
        });
        nextButton.setOnAction(e -> {
            offset += LIMIT;
            displayArtworks();
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> stage.setScene(new LoginScene(stage).getScene()));

        VBox root = new VBox(20, welcomeLabel, menuBar, artworkContainer, prevButton, nextButton, logoutButton);
        root.setAlignment(Pos.CENTER);
        scene = new Scene(root, 800, 600);

        loadArtworksFromAPI();
    }

    public Scene getScene() {
        return scene;
    }

    private void loadArtworksFromAPI() {
        try {
            URL url = new URL("http://localhost:8080/api/artwork/getall");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            reader.close();

            JSONArray arr = new JSONArray(buf.toString());
            allArtworks = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int id = obj.getInt("id");
                String title = obj.getString("title");
                String description = obj.getString("description");
                String imagePath = obj.getString("imagePath");
                String price = String.valueOf(obj.getDouble("price"));
                String date = obj.getString("date");
                JSONObject userObj = obj.getJSONObject("user");
                int artistId = userObj.getInt("id");
                String artistUsername = userObj.getString("username");

                allArtworks.add(new Artwork(id, title, description, price, date, imagePath, artistId, artistUsername));
            }

            displayArtworks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayArtworks() {
        artworkContainer.getChildren().clear();
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        int count = 0;

        int end = Math.min(offset + LIMIT, allArtworks.size());
        List<Artwork> toShow = allArtworks.subList(offset, end);

        for (Artwork art : toShow) {
            VBox box = new VBox(5);
            box.setAlignment(Pos.CENTER);

            Label title = new Label(art.getTitle());
            ImageView iv;
            try {
                String path = "file:" + art.getImagePath().replace("\\", "/");
                iv = new ImageView(new Image(path, 200, 200, true, true));
            } catch (Exception ex) {
                iv = new ImageView();
            }

            iv.setOnMouseClicked(e ->
                    stage.setScene(
                            new ArtworkDetailsScene(stage, art, userId, username, userRole)
                                    .getScene()
                    )
            );

            box.getChildren().addAll(iv, title);
            row.getChildren().add(box);
            count++;

            if (count % LIMIT == 0) {
                artworkContainer.getChildren().add(row);
                row = new HBox(20);
                row.setAlignment(Pos.CENTER);
            }
        }

        if (!row.getChildren().isEmpty()) {
            artworkContainer.getChildren().add(row);
        }

        prevButton.setDisable(offset <= 0);
        nextButton.setDisable(offset + LIMIT >= allArtworks.size());
    }
}
