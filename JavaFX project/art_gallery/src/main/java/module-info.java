module com.example.art_gallery {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.art_gallery to javafx.fxml;
    exports com.example.art_gallery;
    exports com.example.art_gallery.controllers;
}