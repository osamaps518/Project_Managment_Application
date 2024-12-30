package com.hfad2.projectmanagmentapplication.models;

import java.util.Date;
import java.util.UUID;

public class TaskComment {
    private String commentId;
    private String content;
    private User author;  // The person who wrote the comment
    private Task task;    // The task this comment belongs to
    private Date timestamp;

    public TaskComment(User author, Task task, String content) {
        this.commentId = UUID.randomUUID().toString();
        this.author = author;
        this.task = task;
        this.content = content;
        this.timestamp = new Date();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void editComment(String newContent) {
        this.content = newContent;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentId() {
        return commentId;
    }
}