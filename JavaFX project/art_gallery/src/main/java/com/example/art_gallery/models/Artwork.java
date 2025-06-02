package com.example.art_gallery.models;

public class Artwork {
    private int id;
    private String title;
    private String imagePath;
    private String description;
    private String price;
    private String date;
    private int artistId;

    public Artwork(int id, String title, String description, String price, String date, String imagePath, int artistId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.imagePath = imagePath;
        this.artistId = artistId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getDateAdded() {
        return date;
    }

    public int getArtistId() { return artistId; }

}
