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

    $sql = "SELECT u.user_id, u.username, u.email, u.full_name, u.profile_image, u.last_login, u.is_active, pm.role 
        FROM users u 
        JOIN project_members pm ON u.user_id = pm.user_id 
        WHERE pm.project_id = '$project_id' 
        AND pm.role = '$role'";

    $result = $conn->query($sql);
    $members = array();
    
    if($result) {
        while($row = $result->fetch_assoc()) {
            $members[] = $row;
        }
        echo json_encode($members);
    } else {
        echo json_encode(['error' => true, 'message' => $conn->error]);
    }
    
    $database->closeConnection();
}
?>