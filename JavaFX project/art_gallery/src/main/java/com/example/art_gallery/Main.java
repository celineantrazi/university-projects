package com.example.art_gallery;

import com.example.art_gallery.controllers.AuthController;
import javafx.application.Application;
import javafx.stage.Stage;
import com.example.art_gallery.views.SignupScene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        AuthController authController = new AuthController();
        stage.setScene(new SignupScene(stage, authController).getScene());
        stage.setTitle("Art Gallery");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
