package com.hfad2.projectmanagmentapplication.models;


import android.graphics.drawable.Drawable;

// Data class for card items
public class CardData {
    private Drawable image;
    private String imageUrl;
    private String line1;
    private String line2;
    private String line3;
    private Object data; // Original data object (Employee, Task, or Notification)

    public CardData() {

    }

    public CardData(Drawable image, String line1, String line2, String line3, Object data) {
        this.image = image;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.data = data;
    }

    // Getters
    public Drawable getImage() {
        return image;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public Object getData() {
        return data;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setData(Object data) {
        this.data = data;
    }
}