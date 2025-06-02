// src/main/java/com/example/api/Dto/UserReport.java
package com.example.api.dto;

public class UserReport {
    private int artSold;
    private int artBought;
    private double totalIncome;
    private double totalSpent;

    public UserReport(int artSold, int artBought, double totalIncome, double totalSpent) {
        this.artSold = artSold;
        this.artBought = artBought;
        this.totalIncome = totalIncome;
        this.totalSpent = totalSpent;
    }

    public int getArtSold() { return artSold; }
    public int getArtBought() { return artBought; }
    public double getTotalIncome() { return totalIncome; }
    public double getTotalSpent() { return totalSpent; }
}
