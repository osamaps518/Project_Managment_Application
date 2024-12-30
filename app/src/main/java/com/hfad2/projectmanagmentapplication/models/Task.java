package com.hfad2.projectmanagmentapplication.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Task {
    private String taskId;
    private String title;
    private String description;
    private Project project;
    private Employee assignedEmployee;
    private TaskStatus status;
    private TaskPriority priority;
    private Date createdDate;
    private Date dueDate;
    private Date completedDate;
    private int progressPercentage;
    private List<TaskComment> comments;

    public Task(String title, String description, Project project,
                TaskPriority priority, Date dueDate) {
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.project = project;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
        this.createdDate = new Date();
        this.progressPercentage = 0;
        this.comments = new ArrayList<>();
    }


    public void updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        if (newStatus == TaskStatus.COMPLETED) {
            this.completedDate = new Date();
            this.progressPercentage = 100;
        }
    }

    public void updateProgress(int progressPercentage) {
        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        this.progressPercentage = progressPercentage;
    }

    public boolean isOverdue() {
        return new Date().after(dueDate) && status != TaskStatus.COMPLETED;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Project getProject() {
        return project;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public List<TaskComment> getComments() {
        return comments;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}