<?php
require_once '../config/database.php';

if(isset($_GET['user_id']) && isset($_GET['query'])) {
    $user_id = $_GET['user_id'];
    $query = '%' . $_GET['query'] . '%';
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT n.*, 
            sender.full_name as sender_name,
            sender.user_type as sender_type,
            COALESCE(t.title, '') as task_title
            FROM notifications n 
            LEFT JOIN users sender ON n.sender_id = sender.user_id 
            LEFT JOIN tasks t ON n.task_id = t.task_id 
            WHERE (n.title LIKE ? OR sender.full_name LIKE ? OR n.content LIKE ?)
            AND n.sender_id = ?
            ORDER BY n.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssss", $query, $query, $query, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = array();
    while($row = $result->fetch_assoc()) {
        // Add user type-specific information
        if($row['sender_type'] === 'MANAGER') {
            $row['sender_role'] = 'Project Manager';
        } else {
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