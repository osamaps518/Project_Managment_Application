<?php
require_once 'database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $task_id = isset($_POST['task_id']) ? $_POST['task_id'] : "";
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "UPDATE tasks SET status = 'COMPLETED' WHERE task_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $task_id);
    
    $response = array();
    if ($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Task marked as complete!";
    } else {
        $response['error'] = true;
        $response['message'] = "Error: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>