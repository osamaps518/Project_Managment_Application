<?php
/**
 * Retrieves all team members associated with a specific project
 * Method: GET
 * Parameters: project_id
 * Returns: JSON array of team members with their user detail
 */
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Essential headers for API functionality
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

// Include database connection
require_once '../config/database.php';

try {
    // Check if project_id is provided
    if (!isset($_GET['project_id'])) {
        throw new Exception("Project ID is required");
    }
    
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    

    $sql = "SELECT u.*, pm.role FROM users u 
        JOIN project_members pm ON u.user_id = pm.user_id 
        WHERE pm.project_id = '$project_id'";

    $result = $conn->query($sql);
    
    if (!$result) {
        throw new Exception("Database query failed: " . $conn->error);
    }
    
    $members = array();
    while ($row = $result->fetch_assoc()) {
        // Remove sensitive information
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