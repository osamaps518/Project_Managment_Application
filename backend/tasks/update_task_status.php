<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] == "POST") {
    $database = new Database();
    $conn = $database->connect();
    
    // Sanitize inputs
    $task_id = $conn->real_escape_string($_POST['task_id']);
    $status = $conn->real_escape_string($_POST['status']);
    
    // Validate status
    $valid_statuses = array('TODO', 'IN_PROGRESS', 'BLOCKED', 'COMPLETED');
    if (!in_array($status, $valid_statuses)) {
        echo json_encode([
            "error" => true,
            "message" => "Invalid status value"
        ]);
        $database->closeConnection();
        exit;
    }
    
    $sql = "UPDATE tasks SET status = '$status' WHERE task_id = '$task_id'";
    
    if ($conn->query($sql) === TRUE) {
        // Add a notification for status change
        $notification_sql = "INSERT INTO notifications (notification_id, type, sender_id, title, content, task_id) 
                           SELECT UUID(), 'STATUS_UPDATE', t.assigned_to,
                           'Task Status Updated',
                           CONCAT('Task \"', t.title, '\" status changed to ', '$status'),
                           t.task_id
                           FROM tasks t WHERE t.task_id = '$task_id'";
        $conn->query($notification_sql);
        
        echo json_encode([
            "error" => false,
            "message" => "Task status updated successfully"
        ]);
    } else {
        echo json_encode([
            "error" => true,
            "message" => "Error updating task status: " . $conn->error
        ]);
    }
    
    $database->closeConnection();
}