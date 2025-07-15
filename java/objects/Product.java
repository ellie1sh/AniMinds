package com.uilover.project2252.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private double price;
    private double rating;
    private int numberInCart;
    private String extra;
    private String sellerId;
    private String category;

    // Default constructor required for Firebase
    public Product() {
        this.picUrl = new ArrayList<>();
        this.picUrl.add("https://via.placeholder.com/150"); // Default placeholder image
        this.rating = 0.0;
        this.numberInCart = 0;
        this.extra = "";
    }

    public Product(String title, String description, double price, String category, String sellerId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sellerId = sellerId;
        this.picUrl = new ArrayList<>();
        this.picUrl.add("https://via.placeholder.com/150"); // Default placeholder image
        this.rating = 0.0;
        this.numberInCart = 0;
        this.extra = "";
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
