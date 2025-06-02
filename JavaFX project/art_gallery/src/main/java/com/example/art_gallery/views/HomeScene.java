package com.example.art_gallery.views;

import com.example.art_gallery.controllers.AuthController;
import com.example.art_gallery.controllers.DeleteArtworkController;
import com.example.art_gallery.controllers.HomeController;
import com.example.art_gallery.controllers.UploadArtworkController;
import com.example.art_gallery.models.Artwork;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;

public class HomeScene {
    private Stage stage;
    private Scene scene;
    private String userRole;
    private HomeController homeController;
    private VBox artworkContainer;
    private int offset = 0;
    private static final int LIMIT = 5;
    private Button prevButton;
    private Button nextButton;

    public HomeScene(Stage stage, String userRole) {
        this.stage = stage;
        this.userRole = userRole;
        this.homeController = new HomeController();
        Label welcomeLabel = new Label("Welcome to the Art Gallery!");
        welcomeLabel.setFont(new Font(20));
        MenuBar menuBar = new MenuBar();
        if (userRole.equals("artist")) {
            Menu artistMenu = new Menu("Artist Actions");
            MenuItem addArtwork = new MenuItem("add artwork");
            MenuItem deleteArtwork = new MenuItem("delete artwork");
            addArtwork.setOnAction(e -> stage.setScene(new UploadArtworkScene(stage, new UploadArtworkController()).getScene()));

            deleteArtwork.setOnAction(e -> stage.setScene(new DeleteArtworkScene(stage, new DeleteArtworkController()).getScene()));
            artistMenu.getItems().addAll(addArtwork, deleteArtwork);
            menuBar.getMenus().add(artistMenu);
        }

        artworkContainer = new VBox(10);
        artworkContainer.setAlignment(Pos.CENTER);
        prevButton = new Button("previous");
        nextButton = new Button("next");
        prevButton.setOnAction(e -> {
            if (offset > 0) {
                offset -= LIMIT;
                loadArtworks();
            }
        });
        nextButton.setOnAction(e -> {
            offset += LIMIT;
            loadArtworks();
        });

        prevButton.setDisable(offset <= 0);
        nextButton.setDisable(homeController.getArtworksCount() <= offset + LIMIT);
        Button logoutButton = new Button("logout");
        logoutButton.setOnAction(e -> stage.setScene(new LoginScene(stage, new AuthController()).getScene()));
        VBox vb = new VBox(20, welcomeLabel, menuBar, artworkContainer, prevButton, nextButton, logoutButton);
        vb.setAlignment(Pos.CENTER);
        scene = new Scene(vb, 800, 600);
        loadArtworks();
    }

    public Scene getScene() {
        return scene;
    }

    private void loadArtworks() {
        artworkContainer.getChildren().clear();
        List<Artwork> artworks = homeController.getArtworks(offset, LIMIT);
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        int count = 0;

        for (Artwork artwork : artworks) {
            VBox artworkBox = new VBox(5);
            artworkBox.setAlignment(Pos.CENTER);
            Label titleLabel = new Label(artwork.getTitle());
            ImageView artworkImage;
            try {
                String correctedPath = "file:" + artwork.getImagePath().replace("\\", "/");
                Image image = new Image(correctedPath, 200, 200, true, true);
                artworkImage = new ImageView(image);
            } catch (Exception e) {
                artworkImage = new ImageView();
                System.out.println("Failed to load image: " + artwork.getImagePath());
            }
            artworkImage.setOnMouseClicked(e -> stage.setScene(new ArtworkDetailsScene(stage, artwork).getScene()));

            artworkBox.getChildren().addAll(artworkImage, titleLabel);
            row.getChildren().add(artworkBox);
            count++;

            if (count % 5 == 0) {
                artworkContainer.getChildren().add(row);
                row = new HBox(20);
                row.setAlignment(Pos.CENTER);
            }
        }
        if (!row.getChildren().isEmpty()) {
            artworkContainer.getChildren().add(row);
        }
        prevButton.setDisable(offset <= 0);
        nextButton.setDisable(offset + LIMIT >= homeController.getArtworksCount());
    }
}