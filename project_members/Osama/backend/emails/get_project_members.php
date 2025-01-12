<?php
require_once '../config/database.php';

if(isset($_GET['project_id'])) {
    $project_id = $_GET['project_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    // Get both managers and employees with their respective roles
    $sql = "SELECT u.*, u.user_type, 'MANAGER' as role, NULL as employee_status 
        FROM users u 
        JOIN manager_projects mp ON u.user_id = mp.manager_id 
        WHERE mp.project_id = ?
        UNION 
        SELECT u.*, u.user_type, e.role, e.status as employee_status
        FROM users u 
        JOIN employee_projects ep ON u.user_id = ep.employee_id 
        JOIN employees e ON u.user_id = e.user_id
        WHERE ep.project_id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $project_id, $project_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $members = array();
    while($row = $result->fetch_assoc()) {
        unset($row['password']); // Remove sensitive data
        $members[] = $row;
    }
    
    echo json_encode($members);
    $db->closeConnection();
}
?>