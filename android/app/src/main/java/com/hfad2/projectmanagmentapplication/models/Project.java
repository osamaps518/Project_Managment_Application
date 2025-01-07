package com.hfad2.projectmanagmentapplication.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Project {
    private String projectId;
    private String title;
    private String description;
    private ProjectManager manager;
    private List<Employee> teamMembers;
    private List<Task> tasks;
    private ProjectStatus status;
    private Date startDate;
    private Date dueDate;
    private Date completedDate;

    public Project(String title, String description, ProjectManager manager,
                   Date startDate, Date dueDate) {
        this.projectId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.manager = manager;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = ProjectStatus.PLANNING;
        this.teamMembers = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public String getProjectId() {
        return projectId;
    }

    // Project management methods
    public Task createTask(String title, String description,
                           TaskPriority priority, Date dueDate) {
        Task newTask = new Task(title, description, this, priority, dueDate);
        tasks.add(newTask);
        return newTask;
    }

    public double getProgressPercentage() {
        if (tasks.isEmpty()) return 0;

        double totalProgress = tasks.stream()
                .mapToDouble(Task::getProgressPercentage)
                .sum();

        return totalProgress / tasks.size();
    }

    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    // Team management methods
    public void addTeamMember(Employee employee) {
        if (!teamMembers.contains(employee)) {
            teamMembers.add(employee);
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}