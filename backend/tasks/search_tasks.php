<?php
require_once 'database.php';

if(isset($_GET['project_id']) && isset($_GET['query'])) {
    $project_id = $_GET['project_id'];
    $query = '%' . $_GET['query'] . '%';
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT t.*, u.full_name as assigned_name, u.email as assigned_email 
            FROM tasks t 
            LEFT JOIN users u ON t.assigned_to = u.user_id 
            WHERE t.project_id = ? AND (t.title LIKE ? OR t.description LIKE ?)";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sss", $project_id, $query, $query);
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