package com.hfad2.projectmanagmentapplication.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectManager {
    // Composition instead of inheritance
    private User user;
    private List<Project> managedProjects;

    public ProjectManager(User user) {
        this.user = user;
        this.managedProjects = new ArrayList<>();
    }

    // Project management methods
    public Project createProject(String title, String description, Date startDate, Date dueDate) {
        Project newProject = new Project(
                title,
                description,
                this,  // This project manager
                startDate,
                dueDate
        );
        managedProjects.add(newProject);
        return newProject;
    }

    private void assignTaskToEmployee(Task task, Employee employee) {
        if (!employee.isInProject(task.getProject())) {
            throw new IllegalArgumentException(
                    "Cannot assign task to employee who is not part of the project"
            );
        }

        employee.acceptTaskFromManager(task, this);
        task.setAssignedEmployee(employee);
    }

    public boolean isManagingProject(Project project) {
        return managedProjects.stream()
                .anyMatch(p -> p.getProjectId().equals(project.getProjectId()));
    }

    public void addEmployeeToProject(Employee employee, Project project) {
        // First verify this manager has authority over the project
        if (!isManagingProject(project)) {
            throw new IllegalArgumentException(
                    "This project manager is not authorized to modify this project's team"
            );
        }

        // If authorized, proceed with adding the employee
        if (!isTeamMemeber(employee, project)) {
            // Let the employee object handle adding the project to its collection
            employee.acceptProjectFromManager(project, this);
        } else {
            throw new IllegalArgumentException("Employee must be a team member");
        }
    }

    private boolean isTeamMemeber(Employee employee, Project project) {
        return project.getTeamMembers().stream()
                .anyMatch(e -> e.getUserId().equals(employee.getUserId()));
    }

    public void addTeamMember(Employee employee, Project project) {
        project.addTeamMember(employee);
    }

    // Delegate methods to access User information
    public String getUserId() {
        return user.getUserId();
    }
}