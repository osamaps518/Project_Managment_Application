package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;

import java.util.List;

public interface ProgressTrackingRepository {
    void getAllTasks(String projectId, OperationCallback<List<Task>> callback);

    void searchTasks(String projectId, String query, OperationCallback<List<Task>> callback);

    void filterTasksByStatus(String projectId, TaskStatus status, OperationCallback<List<Task>> callback);

    void markTaskAsComplete(String taskId, OperationCallback<Boolean> callback);

    void removeTask(String taskId, OperationCallback<Boolean> callback);
}