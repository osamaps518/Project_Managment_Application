<?php
require_once '../config/database.php';

if(isset($_GET['notification_id'])) {
    $notification_id = $_GET['notification_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    // Removed the type='EMAIL' filter since we want all notifications
    $sql = "SELECT n.*, 
            sender.full_name as sender_name,
            sender.user_type as sender_type,
            CASE 
                WHEN sender.user_type = 'MANAGER' THEN 'Project Manager'
                ELSE e.role 
            END as sender_role
            FROM notifications n 
            JOIN users sender ON n.sender_id = sender.user_id 
            LEFT JOIN employees e ON sender.user_id = e.user_id
            WHERE n.notification_id = ?";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $notification_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if($row = $result->fetch_assoc()) {
        echo json_encode($row);
    } else {
        echo json_encode(["error" => true, "message" => "Notification not found"]);
    }
    
    $db->closeConnection();
}
?>