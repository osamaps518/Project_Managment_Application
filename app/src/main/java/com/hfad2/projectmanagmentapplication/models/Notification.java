package com.hfad2.projectmanagmentapplication.models;


import java.util.Date;

// Notification Model
public class Notification {
    private String id;
    private NotificationType type;
    private String senderName;
    private String title;
    private Date timestamp;

    public Notification(String id, NotificationType type, String senderName,
                        String title, Date timestamp) {
        this.id = id;
        this.type = type;
        this.senderName = senderName;
        this.title = title;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTitle() {
        return title;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}