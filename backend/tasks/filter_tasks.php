<?php
require_once '../config/database.php';

if(isset($_GET['project_manager_id']) && isset($_GET['status'])) {
    $db = new Database();
    $conn = $db->connect();
    
    $project_manager_id = $conn->real_escape_string($_GET['project_manager_id']);
    $status = $conn->real_escape_string($_GET['status']);
    
    $sql = "SELECT t.*, u.full_name as assigned_name, u.email as assigned_email,
            p.title as project_title 
            FROM tasks t
            JOIN projects p ON t.project_id = p.project_id 
            LEFT JOIN users u ON t.assigned_to = u.user_id 
            WHERE p.manager_id = '$project_manager_id' AND t.status = '$status'";
    
    $result = $conn->query($sql);
    $tasks = array();
    
    if($result) {
        while($row = $result->fetch_assoc()) {
            $tasks[] = $row;
        }
        echo json_encode($tasks);
    } else {
        echo json_encode(['error' => true, 'message' => $conn->error]);
    }
    
    $db->closeConnection();
}
?>