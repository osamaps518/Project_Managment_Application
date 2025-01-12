<?php
require_once '../config/database.php';

// Ensure we're receiving a GET request
if ($_SERVER['REQUEST_METHOD'] == "GET") {
    $database = new Database();
    $conn = $database->connect();
    
    // Get and sanitize input parameters
    $project_id = $conn->real_escape_string($_GET['project_id']);
    $search_term = $conn->real_escape_string($_GET['query']);
    
    // Add wildcards for LIKE query
    $search_pattern = "%$search_term%";
    
    // Find employees not already in the project
    $sql = "SELECT u.user_id, u.username, u.full_name, u.user_type,
                   e.role, e.status
            FROM users u
            JOIN employees e ON u.user_id = e.user_id
            LEFT JOIN employee_projects ep 
                ON u.user_id = ep.employee_id 
                AND ep.project_id = ?
            WHERE ep.employee_id IS NULL
            AND (u.full_name LIKE ? OR u.username LIKE ?)
            AND e.status = 'ACTIVE'
            LIMIT 10";  // Limit results for performance
            
    try {
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sss", $project_id, $search_pattern, $search_pattern);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $users = [];
        while ($row = $result->fetch_assoc()) {
            unset($row['password']); // Remove sensitive data
            $users[] = $row;
        }
        
        $response = [
            'error' => false,
            'users' => $users
        ];
        
    } catch (Exception $e) {
        $response = [
            'error' => true,
            'message' => "Error: " . $e->getMessage()
        ];
    }
    
    echo json_encode($response);
    $database->closeConnection();
}
?>