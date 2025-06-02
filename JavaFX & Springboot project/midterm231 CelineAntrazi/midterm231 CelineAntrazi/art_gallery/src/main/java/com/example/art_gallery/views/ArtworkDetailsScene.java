package com.example.art_gallery.views;

import com.example.art_gallery.models.Artwork;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class ArtworkDetailsScene {
    private final Scene scene;

    public ArtworkDetailsScene(Stage stage, Artwork artwork, int loggedInUserId, String loggedInUsername, String loggedInRole) {
        Label titleLabel = new Label(artwork.getTitle());
        Text descriptionText = new Text(artwork.getDescription());
        descriptionText.setWrappingWidth(400);

        Label artistUsernameLabel = new Label("By: " + artwork.getArtistUsername());
        Label priceLabel = new Label("Price: $" + artwork.getPrice());
        Label dateLabel = new Label("Added on: " + artwork.getDateAdded());
        Button buyButton = new Button("Buy");
        Label backLabel = new Label("Back");

        ImageView imageView = new ImageView();
        File imageFile = new File(artwork.getImagePath());
        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } else {
            imageView.setImage(new Image("file:default.jpg"));
        }
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        buyButton.setOnAction(e ->
                stage.setScene(new OrdersScene(stage, artwork, loggedInUserId, loggedInUsername, loggedInRole).getScene())
        );

        backLabel.setOnMouseClicked(e ->
                stage.setScene(new HomeScene(stage, loggedInUserId, loggedInUsername, loggedInRole).getScene())
        );

        VBox vb = new VBox(15, imageView, titleLabel, descriptionText, artistUsernameLabel, priceLabel, dateLabel, buyButton, backLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-padding: 20px;");

        ScrollPane scrollPane = new ScrollPane(vb);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVvalue(0);

        scene = new Scene(scrollPane, 800, 600);
        Platform.runLater(() -> scrollPane.setVvalue(0));
    }

    public Scene getScene() {
        return scene;
    }
}
