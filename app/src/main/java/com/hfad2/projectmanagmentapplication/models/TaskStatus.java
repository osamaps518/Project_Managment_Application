package com.hfad2.projectmanagmentapplication.models;

public enum TaskStatus {
    TODO,           // Task is created but work hasn't started
    IN_PROGRESS,    // Work is actively being done on the task
    BLOCKED,        // Task cannot proceed due to some impediment
    COMPLETED       // Task is finished and approved
}