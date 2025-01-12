
<?php
/**
 * Retrieves all team members associated with a specific project
 * Method: GET
 * Parameters: project_id
 * Returns: JSON array of team members with their user detail
 */


header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

require_once '../config/database.php';

try {
    if (!isset($_GET['project_id'])) {
        throw new Exception("Project ID is required");
    }
    
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    
    // Query now joins with both employee_projects and employees tables
    $sql = "SELECT u.*, e.role, e.status 
            FROM users u 
            JOIN employees e ON u.user_id = e.user_id 
            JOIN employee_projects ep ON e.user_id = ep.employee_id 
            WHERE ep.project_id = '$project_id'
            UNION 
            SELECT u.*, 'MANAGER' as role, 'ACTIVE' as status
            FROM users u 
            JOIN project_managers pm ON u.user_id = pm.user_id
            JOIN manager_projects mp ON pm.user_id = mp.manager_id
            WHERE mp.project_id = '$project_id'";

    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Database query failed: " . $conn->error);
    }
    
    $members = array();
    while ($row = $result->fetch_assoc()) {
        unset($row['password']);
        $members[] = $row;
    }
    
    echo json_encode($members);
    
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(array("error" => $e->getMessage()));
} finally {
    if (isset($database)) {
        $database->closeConnection();
    }
}
?>