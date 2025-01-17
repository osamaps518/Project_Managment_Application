package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;

import java.util.*;


public interface ProgressTrackingRepository {
    void getAllTasks(String projectId, OperationCallback<List<Task>> callback);

    void searchTasks(String projectManagerId, String query, OperationCallback<List<Task>> callback);

    void filterTasksByStatus(String projectManagerId, TaskStatus status, OperationCallback<List<Task>> callback);

    void markTaskAsComplete(String taskId, OperationCallback<Boolean> callback);

    void removeTask(String taskId, OperationCallback<Boolean> callback);

    void createTask(String projectId, String title, String description,
                           TaskPriority priority, Date dueDate, String assignedTo,
                           OperationCallback<Boolean> callback);
}