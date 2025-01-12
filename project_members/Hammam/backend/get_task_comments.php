<?php
require_once '../config/database.php';

if(isset($_GET['task_id'])) {
    $task_id = $_GET['task_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT c.*, u.full_name, u.email 
            FROM task_comments c
            JOIN users u ON c.author_id = u.user_id 
            WHERE c.task_id = ?
            ORDER BY c.timestamp DESC";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $task_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $comments = array();
    while($row = $result->fetch_assoc()) {
        $comments[] = $row;
    }
    
    echo json_encode($comments);
    $db->closeConnection();
}
?>