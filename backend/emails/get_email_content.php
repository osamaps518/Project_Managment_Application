<?php
require_once '../config/database.php';

if(isset($_GET['notification_id'])) {
    $notification_id = $_GET['notification_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT n.*, u.full_name as sender_name 
            FROM notifications n 
            LEFT JOIN users u ON n.sender_id = u.user_id 
            WHERE n.notification_id = ? AND n.type = 'EMAIL'";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $notification_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if($row = $result->fetch_assoc()) {
        echo json_encode($row);
    } else {
        echo json_encode(["error" => true, "message" => "Email not found"]);
    }
    
    $db->closeConnection();
}
?>