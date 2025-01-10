package com.hfad2.projectmanagmentapplication.models;


import java.util.Date;

// Notification Model
public class Notification {
    private String id;
    private NotificationType type;
    private String senderName;
    private String senderID;
    private String receiverID;
    private String title;
    private String taskId;
    private Date timestamp;
    private boolean isArchived;
    private String content;
    private String projectId;



    // Constructor for EmailDetail Activity
    public Notification(String id, NotificationType type, String senderName,
                        String title, Date timestamp, String senderId,
                        String receiverId, String taskId, String projectId) {
        this.id = id;
        this.type = type;
        this.senderName = senderName;
        this.title = title;
        this.timestamp = timestamp;
        this.senderID = senderId;
        this.receiverID = receiverId;
        this.taskId = taskId;
        this.projectId = projectId;
    }

    public Notification(String id, NotificationType type, String senderName,
                        String title, Date timestamp, String senderID, String
                                receiverID, String taskId) {
        this.id = id;
        this.type = type;
        this.senderName = senderName;
        this.title = title;
        this.timestamp = timestamp;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getContent() {
        return content;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
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

    public String getSenderId() {
        return senderID;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverId() {
        return receiverID;
    }
}