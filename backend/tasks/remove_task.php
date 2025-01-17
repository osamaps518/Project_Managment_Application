<?php
require_once '../config/database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $task_id = isset($_POST['task_id']) ? $_POST['task_id'] : "";
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "DELETE FROM tasks WHERE task_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $task_id);
    
    $response = array();
    if ($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Task removed successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Error: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>