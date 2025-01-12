<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] != "GET") {
    echo json_encode([
        "error" => true,
        "message" => "Invalid request method"
    ]);
    exit;
}

if (!isset($_GET['project_id']) || !isset($_GET['employee_id'])) {
    echo json_encode([
        "error" => true,
        "message" => "Missing required parameters"
    ]);
    exit;
}

try {
    $db = new Database();
    $conn = $db->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    $employee_id = $conn->real_escape_string($_GET['employee_id']);
    
    // Join with tasks and projects to get task and project details
    $sql = "SELECT t.*, p.title as project_title 
            FROM tasks t
            JOIN projects p ON t.project_id = p.project_id
            WHERE t.project_id = ? 
            AND t.assigned_to = ? 
            AND t.status != 'COMPLETED'
            LIMIT 1";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $project_id, $employee_id);
    
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        
        echo json_encode([
            "error" => false,
            "data" => $row  // This will be null if no task is found
        ]);
    } else {
        throw new Exception("Error executing query: " . $stmt->error);
    }
    
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