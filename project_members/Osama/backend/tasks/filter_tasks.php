<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] != "GET") {
    echo json_encode([
        "error" => true,
        "message" => "Invalid request method"
    ]);
    exit;
}

if (!isset($_GET['manager_id']) || !isset($_GET['status'])) {
    echo json_encode([
        "error" => true,
        "message" => "Manager ID and status are required"
    ]);
    exit;
}

try {
    $db = new Database();
    $conn = $db->connect();
    
    $manager_id = $conn->real_escape_string($_GET['manager_id']);
    $status = $conn->real_escape_string($_GET['status']);
    
    // Updated query to match get_tasks.php structure and include all necessary fields
    $sql = "SELECT t.*, 
            u.user_id, u.full_name, u.username, u.user_type,
            p.title as project_title, p.project_id
            FROM tasks t 
            JOIN projects p ON t.project_id = p.project_id
            JOIN manager_projects mp ON p.project_id = mp.project_id
            LEFT JOIN users u ON t.assigned_to = u.user_id 
            WHERE mp.manager_id = ? AND t.status = ?
            ORDER BY t.due_date ASC";
    
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception("Failed to prepare statement: " . $conn->error);
    }
    
    $stmt->bind_param("ss", $manager_id, $status);
    
    if (!$stmt->execute()) {
        throw new Exception("Failed to execute query: " . $stmt->error);
    }
    
    $result = $stmt->get_result();
    $tasks = array();
    
    while ($row = $result->fetch_assoc()) {
        // Add default values for potentially null fields
        if (!isset($row['role'])) {
            $row['role'] = 'Employee';
        }
        $tasks[] = $row;
    }
    
    // Return properly structured response
    header('Content-Type: application/json');
    echo json_encode([
        "error" => false,
        "data" => $tasks
    ]);

} catch (Exception $e) {
    echo json_encode([
        "error" => true,
        "message" => $e->getMessage()
    ]);
} finally {
    if (isset($db)) {
        $db->closeConnection();
    }
}
?>