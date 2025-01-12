<?php
require_once '../config/database.php';

if(isset($_GET['manager_id'])) {
    $manager_id = $_GET['manager_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT t.*, 
        u.user_id, u.full_name, u.username, u.user_type,
        p.title as project_title, p.project_id
        FROM tasks t 
        JOIN projects p ON t.project_id = p.project_id
        JOIN manager_projects mp ON p.project_id = mp.project_id
        LEFT JOIN users u ON t.assigned_to = u.user_id 
        WHERE mp.manager_id = ?
        ORDER BY t.due_date ASC";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $manager_id);
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