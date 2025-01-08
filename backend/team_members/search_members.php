<?php
/**
 * Searches for team members by name or username within a project
 * Method: GET
 * Parameters: project_id, query (search string)
 * Returns: JSON array of matching team members
 */

require_once '../config/database.php';

if(isset($_GET['project_id']) && isset($_GET['query'])) {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    $query = $conn->real_escape_string($_GET['query']);
    
    $sql = "SELECT DISTINCT u.user_id, u.username, u.email, u.full_name, 
            u.profile_image, u.last_login, u.is_active, pm.role 
            FROM users u 
            JOIN project_members pm ON u.user_id = pm.user_id 
            WHERE pm.project_id = '$project_id' 
            AND (LOWER(u.full_name) LIKE LOWER('%$query%') 
            OR LOWER(u.username) LIKE LOWER('%$query%'))";
    
    $result = $conn->query($sql);
    
    if($result) {
        $members = array();
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
