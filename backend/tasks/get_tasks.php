<?php
require_once '../config/database.php';

if(isset($_GET['project_manager_id'])) {
    $project_manager_id = $_GET['project_manager_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT t.*, u.full_name as assigned_name, u.email as assigned_email,
            p.title as project_title 
            FROM tasks t 
            JOIN projects p ON t.project_id = p.project_id
            LEFT JOIN users u ON t.assigned_to = u.user_id 
            WHERE p.manager_id = ?";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $project_manager_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $tasks = array();
    while($row = $result->fetch_assoc()) {
        $tasks[] = $row;
    }
    
    echo json_encode($tasks);
    $db->closeConnection();
}
?>