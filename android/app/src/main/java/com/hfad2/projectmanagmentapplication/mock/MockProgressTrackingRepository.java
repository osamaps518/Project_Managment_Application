package com.hfad2.projectmanagmentapplication.mock;

import android.os.Handler;
import android.os.Looper;

import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.ProgressTrackingRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MockProgressTrackingRepository implements ProgressTrackingRepository {
    private final MockDataGenerator mockData;
    private final Handler mainHandler;

    public MockProgressTrackingRepository() {
        this.mockData = new MockDataGenerator();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getAllTasks(String projectId, OperationCallback<List<Task>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                callback.onSuccess(project.getTasks());
            } catch (Exception e) {
                callback.onError("Failed to load tasks: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void searchTasks(String projectId, String query, OperationCallback<List<Task>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                List<Task> filteredTasks = project.getTasks().stream()
                        .filter(t -> t.getTitle().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                callback.onSuccess(filteredTasks);
            } catch (Exception e) {
                callback.onError("Search failed: " + e.getMessage());
            }
        }, 300);
    }

    @Override
    public void filterTasksByStatus(String projectId, TaskStatus status, OperationCallback<List<Task>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                List<Task> filteredTasks = project.getTasks().stream()
                        .filter(t -> t.getStatus() == status)
                        .collect(Collectors.toList());
                callback.onSuccess(filteredTasks);
            } catch (Exception e) {
                callback.onError("Filter failed: " + e.getMessage());
            }
        }, 300);
    }

    @Override
    public void markTaskAsComplete(String taskId, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Task task = findTask(taskId);
                task.updateStatus(TaskStatus.COMPLETED);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to mark task as complete: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void removeTask(String taskId, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Task taskToRemove = findTask(taskId);
                Project project = taskToRemove.getProject();
                project.getTasks().removeIf(t -> t.getTaskId().equals(taskId));
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to remove task: " + e.getMessage());
            }
        }, 500);
    }

    private Project findProject(String projectId) {
        return mockData.getProjects().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private Task findTask(String taskId) {
        return mockData.getTasks().stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    @Override
    public Task createTask(String projectId, String title, String description, TaskPriority priority,
                           Date dueDate) {
        Project project = findProject(projectId);
        Task task = project.createTask(title, description, priority, dueDate);
        return task;
    }
}