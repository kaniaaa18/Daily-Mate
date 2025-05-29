package com.example.dailymate.Data.entity;

import android.graphics.Bitmap;

public class Note {
    private int id;
    private String title;
    private String description;
    private long time;
    private byte[] image;

    public Note(int id, String title, String description, long time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
    }

    // Add this new constructor to handle images
    public Note(int id, String title, String description, long time, byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.image = image;
    }

    // Add getters and setters for image
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // Existing getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTime() { return time; }
}