<?php
require_once '../config/database.php';

// Check if we have either task_id OR (show_archived AND user_id)
if(isset($_GET['task_id']) || (isset($_GET['show_archived']) && isset($_GET['user_id']))) {
    $db = new Database();
    $conn = $db->connect();
    
    // Base SQL query
    $sql = "SELECT n.*, 
            sender.full_name as sender_name,
            sender.user_type as sender_type,
            COALESCE(t.title, '') as task_title
            FROM notifications n 
            LEFT JOIN users sender ON n.sender_id = sender.user_id 
            LEFT JOIN tasks t ON n.task_id = t.task_id 
            WHERE 1=1";  // This allows us to dynamically add conditions
    
    $params = array();
    $types = "";
    
    // Task-specific comments
    if(isset($_GET['task_id'])) {
        $sql .= " AND n.task_id = ? AND n.type = 'COMMENT'";
        $params[] = $_GET['task_id'];
        $types .= "s";
    }
    // General notifications
    else {
        $show_archived = $_GET['show_archived'] === 'true' ? true : false;
        $user_id = $_GET['user_id'];
        
        $sql .= " AND n.is_archived = ? AND n.receiver_id = ?";
        $params[] = $show_archived ? 1 : 0;
        $params[] = $user_id;
        $types .= "is";
    }
    
    $sql .= " ORDER BY n.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->bind_param($types, ...$params);
    }
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
?>