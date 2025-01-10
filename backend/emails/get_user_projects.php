<?php
require_once '../config/database.php';

if(isset($_GET['user_id'])) {
    $user_id = $_GET['user_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT p.* FROM projects p 
            JOIN project_members pm ON p.project_id = pm.project_id 
            WHERE pm.user_id = ?";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $projects = array();
    while($row = $result->fetch_assoc()) {
        $projects[] = $row;
    }
    
    echo json_encode($projects);
    $db->closeConnection();
}
?>