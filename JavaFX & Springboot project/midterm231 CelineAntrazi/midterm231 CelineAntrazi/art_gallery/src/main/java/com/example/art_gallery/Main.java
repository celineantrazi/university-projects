package com.example.art_gallery;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.art_gallery.views.SignupScene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // No need to pass AuthController anymore
        stage.setScene(new SignupScene(stage).getScene());
        stage.setTitle("Art Gallery");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
