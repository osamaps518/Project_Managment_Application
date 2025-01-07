<?php
require_once 'database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $project_id = isset($_POST['project_id']) ? $_POST['project_id'] : "";
    $title = isset($_POST['title']) ? $_POST['title'] : "";
    $description = isset($_POST['description']) ? $_POST['description'] : "";
    $priority = isset($_POST['priority']) ? $_POST['priority'] : "";
    $due_date = isset($_POST['due_date']) ? $_POST['due_date'] : "";
    $assigned_to = isset($_POST['assigned_to']) ? $_POST['assigned_to'] : "";
    $task_id = uniqid();
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "INSERT INTO tasks (task_id, project_id, title, description, priority, due_date, assigned_to, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'TODO')";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssss", $task_id, $project_id, $title, $description, $priority, $due_date, $assigned_to);
    
    $response = array();
    if ($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Task added successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Error: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>