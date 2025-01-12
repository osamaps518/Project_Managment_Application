<?php
/**
 *  Filters team members by their role in a project
 *  Method: GET
 *  Parameters: project_id, role
 *  Returns: JSON array of team members with specified role
 */


require_once '../config/database.php';

if(isset($_GET['project_id']) && isset($_GET['role'])) {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    $role = $conn->real_escape_string($_GET['role']);

    if($role === 'MANAGER') {
        $sql = "SELECT u.*, 'MANAGER' as role, 'ACTIVE' as status
            FROM users u 
            JOIN project_managers pm ON u.user_id = pm.user_id 
            JOIN manager_projects mp ON pm.user_id = mp.manager_id
            WHERE mp.project_id = '$project_id'";
    } else {
        $sql = "SELECT u.*, e.role, e.status 
            FROM users u 
            JOIN employees e ON u.user_id = e.user_id 
            JOIN employee_projects ep ON e.user_id = ep.employee_id 
            WHERE ep.project_id = '$project_id' 
            AND e.role = '$role'";
    }

    $result = $conn->query($sql);
    $members = array();
    
    if($result) {
        while($row = $result->fetch_assoc()) {
            unset($row['password']);
            $members[] = $row;
        }
        echo json_encode($members);
    } else {
        echo json_encode(['error' => true, 'message' => $conn->error]);
    }
    
    $database->closeConnection();
}
?>