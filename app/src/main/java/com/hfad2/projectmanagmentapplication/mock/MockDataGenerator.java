package com.hfad2.projectmanagmentapplication.mock;

import com.hfad2.projectmanagmentapplication.models.*;

import java.util.*;

public class MockDataGenerator {
    // Store references to maintain relationships
    private List<User> users;
    private List<ProjectManager> projectManagers;
    private List<Employee> employees;
    private List<Project> projects;
    private List<Task> tasks;

    public MockDataGenerator() {
        this.users = new ArrayList<>();
        this.projectManagers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.tasks = new ArrayList<>();
        generateMockData();
    }

    private void generateMockData() {
        // Create Users
        User user1 = new User("john.doe@company.com", "John Doe");
        user1.username = "johndoe";
        User user2 = new User("jane.smith@company.com", "Jane Smith");
        user2.username = "janesmith";
        User user3 = new User("bob.wilson@company.com", "Bob Wilson");
        user3.username = "bobwilson";
        User user4 = new User("alice.johnson@company.com", "Alice Johnson");
        user4.username = "alicej";

        users.addAll(Arrays.asList(user1, user2, user3, user4));

        // Create Project Managers
        ProjectManager pm1 = new ProjectManager(user1);
        ProjectManager pm2 = new ProjectManager(user2);
        projectManagers.addAll(Arrays.asList(pm1, pm2));

        // Create Employees
        Employee emp1 = new Employee(user3);
        Employee emp2 = new Employee(user4);
        employees.addAll(Arrays.asList(emp1, emp2));

        // Create Projects
        Calendar cal = Calendar.getInstance();

        // Project 1: Active project with multiple tasks
        Project project1 = pm1.createProject(
                "Website Redesign",
                "Complete overhaul of company website with modern design",
                new Date(),
                addDays(new Date(), 30)
        );

        // Project 2: Planning phase project
        Project project2 = pm2.createProject(
                "Mobile App Development",
                "Create new mobile app for customer engagement",
                addDays(new Date(), 7),
                addDays(new Date(), 60)
        );

        projects.addAll(Arrays.asList(project1, project2));

        // Add team members to projects
        pm1.addTeamMember(emp1, project1);
        pm1.addTeamMember(emp2, project1);
        pm2.addTeamMember(emp1, project2);

        // Create Tasks for Project 1
        Task task1 = project1.createTask(
                "Homepage Design",
                "Design new homepage layout with improved UX",
                TaskPriority.HIGH,
                addDays(new Date(), 7)
        );
        task1.updateProgress(75);
        task1.updateStatus(TaskStatus.IN_PROGRESS);

        Task task2 = project1.createTask(
                "Backend API Development",
                "Develop RESTful API endpoints for new features",
                TaskPriority.MEDIUM,
                addDays(new Date(), 14)
        );
        task2.updateProgress(30);
        task2.updateStatus(TaskStatus.IN_PROGRESS);

        // Create Tasks for Project 2
        Task task3 = project2.createTask(
                "Requirements Analysis",
                "Gather and document app requirements",
                TaskPriority.HIGH,
                addDays(new Date(), 5)
        );
        task3.updateStatus(TaskStatus.TODO);

        tasks.addAll(Arrays.asList(task1, task2, task3));

        // Assign tasks to employees
        try {
            pm1.assignTaskToEmployee(task1, emp1);
            pm1.assignTaskToEmployee(task2, emp2);
            pm2.assignTaskToEmployee(task3, emp1);
        } catch (IllegalArgumentException e) {
            // Handle assignment errors
            System.err.println("Error assigning tasks: " + e.getMessage());
        }

        // Add some task comments
        TaskComment comment1 = new TaskComment(user3, task1,
                "Updated the color scheme based on brand guidelines");
        TaskComment comment2 = new TaskComment(user1, task1,
                "Looks good, please proceed with the implementation");
    }

    // Utility method to add days to a date
    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    // Getter methods for accessing mock data
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<ProjectManager> getProjectManagers() {
        return new ArrayList<>(projectManagers);
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }
}