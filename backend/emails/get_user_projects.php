<?php
require_once '../config/database.php';

if(isset($_GET['user_id'])) {
    $user_id = $_GET['user_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $userTypeQuery = "SELECT user_type FROM users WHERE user_id = ?";
    $stmt = $conn->prepare($userTypeQuery);
    $stmt->bind_param("s", $user_id);
    $stmt->execute();
    $userResult = $stmt->get_result();
    $userType = strtoupper($userResult->fetch_assoc()['user_type']);
    
    if($userType === 'MANAGER') {
        $sql = "SELECT p.* FROM projects p 
                JOIN manager_projects mp ON p.project_id = mp.project_id 
                WHERE mp.manager_id = ?";
    } else {
        $sql = "SELECT p.* FROM projects p 
                JOIN employee_projects ep ON p.project_id = ep.project_id 
                WHERE ep.employee_id = ?";
    }
    
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