<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] == "GET") {
    $database = new Database();
    $conn = $database->connect();
    
    // Sanitize input
    $task_id = $conn->real_escape_string($_GET['task_id']);
    
    $sql = "SELECT * FROM tasks WHERE task_id = '$task_id'";
    $result = $conn->query($sql);
    
    if ($result && $result->num_rows > 0) {
        $task = $result->fetch_assoc();
        echo json_encode($task);
    } else {
        echo json_encode([
            "error" => true,
            "message" => "Task not found"
        ]);
    }
    
    $database->closeConnection();
}