<?php
require_once '../config/database.php';

if(isset($_GET['project_id'])) {
    $project_id = $_GET['project_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "SELECT u.* FROM users u 
            JOIN project_members pm ON u.user_id = pm.user_id 
            WHERE pm.project_id = ?";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $project_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $members = array();
    while($row = $result->fetch_assoc()) {
        $members[] = $row;
    }
    
    echo json_encode($members);
    $db->closeConnection();
}
?>