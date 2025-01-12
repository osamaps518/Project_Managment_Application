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
    
    $sql = "SELECT DISTINCT u.*, e.role, e.status 
            FROM users u 
            JOIN employees e ON u.user_id = e.user_id 
            JOIN employee_projects ep ON e.user_id = ep.employee_id 
            WHERE ep.project_id = '$project_id' 
            AND (LOWER(u.full_name) LIKE LOWER('%$query%') 
            OR LOWER(u.username) LIKE LOWER('%$query%'))
            UNION
            SELECT u.*, 'MANAGER' as role, 'ACTIVE' as status
            FROM users u 
            JOIN project_managers pm ON u.user_id = pm.user_id
            JOIN manager_projects mp ON pm.user_id = mp.manager_id
            WHERE mp.project_id = '$project_id'
            AND (LOWER(u.full_name) LIKE LOWER('%$query%') 
            OR LOWER(u.username) LIKE LOWER('%$query%'))";
    
    $result = $conn->query($sql);
    
    if($result) {
        $members = array();
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