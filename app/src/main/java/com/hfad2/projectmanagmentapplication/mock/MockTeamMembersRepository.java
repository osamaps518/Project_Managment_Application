package com.hfad2.projectmanagmentapplication.mock;

import android.os.Handler;
import android.os.Looper;

import com.hfad2.projectmanagmentapplication.models.Employee;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.ProjectManager;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.TeamMembersRepository;

import java.util.List;
import java.util.stream.Collectors;

public class MockTeamMembersRepository implements TeamMembersRepository {
    private final MockDataGenerator mockData;
    private final Handler mainHandler;

    public MockTeamMembersRepository() {
        this.mockData = new MockDataGenerator();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getAllMembers(String projectId, OperationCallback<List<Employee>> callback) {
        // Simulate network delay
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                callback.onSuccess(project.getTeamMembers());
            } catch (Exception e) {
                callback.onError("Failed to load team members: " + e.getMessage());
            }
        }, 500); // 500ms delay
    }

    @Override
    public void addMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                Employee employee = findEmployee(employeeId);

                if (project.getTeamMembers().contains(employee)) {
                    callback.onError("Employee already in team");
                    return;
                }

                project.addTeamMember(employee);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to add member: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void removeMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                project.getTeamMembers().removeIf(e -> e.getUserId().equals(employeeId));
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to remove member: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void searchMembers(String projectId, String query, OperationCallback<List<Employee>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Project project = findProject(projectId);
                List<Employee> filteredMembers = project.getTeamMembers().stream()
                        .filter(e -> e.getFullName().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                callback.onSuccess(filteredMembers);
            } catch (Exception e) {
                callback.onError("Search failed: " + e.getMessage());
            }
        }, 300);
    }

    @Override
    public void filterMembersByRole(String projectId, String role, OperationCallback<List<Employee>> callback) {
        // For now, return all members since roles aren't implemented in mock data
        getAllMembers(projectId, callback);
    }

    @Override
    public void assignTask(String projectId, String employeeId, Task task, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Employee employee = findEmployee(employeeId);
                ProjectManager manager = findProjectManager(projectId);
                manager.assignTaskToEmployee(task, employee);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to assign task: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void getAssignedTask(String projectId, String employeeId, OperationCallback<Task> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Employee employee = findEmployee(employeeId);
                List<Task> tasks = employee.getAssignedTasks();
                Task assignedTask = tasks.stream()
                        .filter(t -> t.getProject().getProjectId().equals(projectId))
                        .findFirst()
                        .orElse(null);

                if (assignedTask != null) {
                    callback.onSuccess(assignedTask);
                } else {
                    callback.onError("No task assigned");
                }
            } catch (Exception e) {
                callback.onError("Failed to get task: " + e.getMessage());
            }
        }, 500);
    }

    private Project findProject(String projectId) {
        return mockData.getProjects().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private Employee findEmployee(String employeeId) {
        return mockData.getEmployees().stream()
                .filter(e -> e.getUserId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    private ProjectManager findProjectManager(String projectId) {
        Project project = findProject(projectId);
        return mockData.getProjectManagers().stream()
                .filter(pm -> pm.isManagingProject(project))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project manager not found"));
    }

    @Override
    public void findEmployee(String userId, OperationCallback<Employee> callback) {
        mainHandler.postDelayed(() -> {
            try {
                Employee employee = mockData.getEmployees().stream()
                        .filter(e -> e.getUserId().equals(userId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
                callback.onSuccess(employee);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }, 500);
    }
}