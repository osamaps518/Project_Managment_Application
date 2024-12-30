package com.hfad2.projectmanagmentapplication.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Employee {
    // Composition instead of inheritance
    private User user;
    // ProjectID and the Project itself
    private Map<String, Project> assignedProjects;
    private List<Task> assignedTasks;
    private EmployeeStatus status;
    // Track progress per project

    public Employee(User user) {
        this.user = user;
        this.assignedProjects = new HashMap<String, Project>();
        this.assignedTasks = new ArrayList<>();
        this.status = EmployeeStatus.AVAILABLE;
    }

    public void updateTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
    }

    public boolean isInProject(Project project) {
        return assignedProjects.containsKey(project.getProjectId());
    }

    public List<Project> getProjectsSorted() {
        // Create temporary list for sorting
        return new ArrayList<>(assignedProjects.values())
                .stream()
                .sorted(Comparator.comparing(Project::getStartDate))
                .collect(Collectors.toList());
    }

    // Delegate methods to access User information
    public String getUserId() {
        return user.getUserId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getUserName() {
        return user.getUsername();
    }

    // Return the list without allowing external classes to modify it
    public List<Task> getAssignedTasks() {
        return Collections.unmodifiableList(assignedTasks);
    }


    // Provide a way for ProjectManager to assign tasks
    // This is the key part
    void acceptTaskFromManager(Task task, ProjectManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Task must be assigned by a project manager");
        }

        if (!manager.isManagingProject(task.getProject())) {
            throw new IllegalArgumentException("Project manager is not authorized for this project");
        }
        addAssignedTask(task);
    }

    // Provide a way for ProjectManager to assign projects
    void acceptProjectFromManager(Project project, ProjectManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Project must be assigned by a project manager");
        }

        if (!manager.isManagingProject(project)) {
            throw new IllegalArgumentException("Project manager is not authorized for this project");
        }
        addAssignedProject(project);
    }

    // can only be called by acceptProjectFromManager, to deny employees from
    // assigning projects to themselves
    private void addAssignedProject(Project project) {
        assignedProjects.put(project.getProjectId(), project);
    }

    // can only be called by acceptTaskFromManager, to deny employees from
    // assigning tasks to themselves
    private void addAssignedTask(Task task) {
        assignedTasks.add(task);
    }
}