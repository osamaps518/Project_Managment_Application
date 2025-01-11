<?php
require_once '../config/database.php';

if(isset($_GET['show_archived']) && isset($_GET['user_id'])) {
    $show_archived = $_GET['show_archived'] === 'true' ? true : false;
    $user_id = $_GET['user_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT n.*, 
            sender.full_name as sender_name,
            sender.user_type as sender_type,
            COALESCE(t.title, '') as task_title
            FROM notifications n 
            LEFT JOIN users sender ON n.sender_id = sender.user_id 
            LEFT JOIN tasks t ON n.task_id = t.task_id 
            WHERE n.is_archived = ? AND n.sender_id = ?
            ORDER BY n.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    $archived = $show_archived ? 1 : 0;
    $stmt->bind_param("is", $archived, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = array();
    while($row = $result->fetch_assoc()) {
        // Add user type-specific information
        if($row['sender_type'] === 'MANAGER') {
            $row['sender_role'] = 'Project Manager';
        } else {
            // Get employee role from employees table
            $roleQuery = "SELECT role FROM employees WHERE user_id = ?";
            $roleStmt = $conn->prepare($roleQuery);
            $roleStmt->bind_param("s", $row['sender_id']);
            $roleStmt->execute();
            $roleResult = $roleStmt->get_result();
            if($roleRow = $roleResult->fetch_assoc()) {
                $row['sender_role'] = $roleRow['role'];
            }
        }
        $notifications[] = $row;
    }
    
    echo json_encode($notifications);
    $db->closeConnection();
}