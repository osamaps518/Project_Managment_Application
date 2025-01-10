<?php
require_once '../config/database.php';

if(isset($_GET['show_archived']) && isset($_GET['user_id'])) {
    $show_archived = $_GET['show_archived'] === 'true' ? true : false;
    $user_id = $_GET['user_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT n.*, u.full_name as sender_name 
            FROM notifications n 
            LEFT JOIN users u ON n.sender_id = u.user_id 
            WHERE n.is_archived = ? AND n.receiver_id = ?
            ORDER BY n.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    $archived = $show_archived ? 1 : 0;
    $stmt->bind_param("is", $archived, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = array();
    while($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }
    
    echo json_encode($notifications);
    $db->closeConnection();
}
?>