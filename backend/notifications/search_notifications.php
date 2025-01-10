<?php
require_once '../config/database.php';

if(isset($_GET['user_id']) && isset($_GET['query'])) {
    $user_id = $_GET['user_id'];
    $query = '%' . $_GET['query'] . '%';
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT n.*, u.full_name as sender_name 
            FROM notifications n 
            LEFT JOIN users u ON n.sender_id = u.user_id 
            WHERE (n.title LIKE ? OR u.full_name LIKE ?)
            ORDER BY n.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $query, $query);
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