<?php
require_once '../config/database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    // Get all required parameters
    $project_id = isset($_POST['project_id']) ? $_POST['project_id'] : null;
    $title = isset($_POST['title']) ? $_POST['title'] : null;
    $description = isset($_POST['description']) ? $_POST['description'] : null;
    $priority = isset($_POST['priority']) ? $_POST['priority'] : null;
    $due_date = isset($_POST['due_date']) ? $_POST['due_date'] : null;
    $assigned_to = isset($_POST['assigned_to']) ? $_POST['assigned_to'] : null;
    
    // Validate that all required fields are present
    if (!$project_id || !$title || !$description || !$priority || !$due_date || !$assigned_to) {
        echo json_encode([
            "error" => true,
            "message" => "Missing required fields"
        ]);
        exit;
    }
    
    $db = new Database();
    $conn = $db->connect();
    
    // First verify that the assigned user is an employee in this project
    $verify_sql = "SELECT COUNT(*) as count 
                   FROM employee_projects 
                   WHERE employee_id = ? AND project_id = ?";
    $stmt = $conn->prepare($verify_sql);
    $stmt->bind_param("ss", $assigned_to, $project_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $count = $result->fetch_assoc()['count'];
    
    if ($count == 0) {
        echo json_encode([
            "error" => true,
            "message" => "Invalid assignment: User is not a member of this project"
        ]);
        exit;
    }

    // Check if employee already has a task in this project
    $check_existing_task = "SELECT COUNT(*) as task_count 
                           FROM tasks 
                           WHERE project_id = ? 
                           AND assigned_to = ? 
                           AND status != 'COMPLETED'";
    
    $stmt = $conn->prepare($check_existing_task);
    $stmt->bind_param("ss", $project_id, $assigned_to);
    $stmt->execute();
    $result = $stmt->get_result();
    $existing_tasks = $result->fetch_assoc()['task_count'];
    
    if ($existing_tasks > 0) {
        echo json_encode([
            "error" => true,
            "message" => "Employee already has an active task in this project"
        ]);
        exit;
    }
    
    // Generate a UUID for the task
    $task_id = uniqid();
    
    // Create the task
    $sql = "INSERT INTO tasks (task_id, project_id, title, description, priority, 
            due_date, assigned_to, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, 'TODO')";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssss", $task_id, $project_id, $title, $description, 
                      $priority, $due_date, $assigned_to);
    
    if ($stmt->execute()) {
        echo json_encode([
            "error" => false,
            "message" => "Task added successfully!",
            "task_id" => $task_id
        ]);
    } else {
        echo json_encode([
            "error" => true,
            "message" => "Error creating task: " . $conn->error
        ]);
    }
    
    $db->closeConnection();
}
?>