package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Employee;

import java.util.List;

public interface TeamMembersRepository {
    // Core operations
    void getAllMembers(String projectId, OperationCallback<List<Employee>> callback);

    void addMember(String projectId, String employeeId, OperationCallback<Boolean> callback);

    void removeMember(String projectId, String employeeId, OperationCallback<Boolean> callback);

    // Search/Filter operations
    void searchMembers(String projectId, String query, OperationCallback<List<Employee>> callback);

    void filterMembersByRole(String projectId, String role, OperationCallback<List<Employee>> callback);

    // Task management
    void assignTask(String projectId, String employeeId, Task task, OperationCallback<Boolean> callback);

    void getAssignedTask(String projectId, String employeeId, OperationCallback<Task> callback);
}

// Callback interface
public interface OperationCallback<T> {
    void onSuccess(T result);

    void onError(String error);
}