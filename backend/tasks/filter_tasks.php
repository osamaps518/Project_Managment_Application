<?php
require_once '../config/database.php';

if(isset($_GET['manager_id']) && isset($_GET['status'])) {
    $db = new Database();
    $conn = $db->connect();
    
    $manager_id = $conn->real_escape_string($_GET['manager_id']);
    $status = $conn->real_escape_string($_GET['status']);
    
    // Updated query to use new schema's structure
    $sql = "SELECT t.*, u.full_name as assigned_name, u.username,
            p.title as project_title 
            FROM tasks t
            JOIN projects p ON t.project_id = p.project_id 
            JOIN manager_projects mp ON p.project_id = mp.project_id
            LEFT JOIN users u ON t.assigned_to = u.user_id 
            WHERE mp.manager_id = ? AND t.status = ?
            ORDER BY t.due_date ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $manager_id, $status);
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