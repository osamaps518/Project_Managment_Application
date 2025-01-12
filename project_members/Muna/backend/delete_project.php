<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $project_id = $_POST['project_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    // Start transaction since we're deleting from multiple tables
    $conn->begin_transaction();
    
    try {
        // First delete all notifications related to project tasks
        $sql = "DELETE n FROM notifications n 
                INNER JOIN tasks t ON n.task_id = t.task_id 
                WHERE t.project_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $project_id);
        $stmt->execute();
        
        // Delete all tasks associated with the project
        $sql = "DELETE FROM tasks WHERE project_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $project_id);
        $stmt->execute();
        
        // Delete all employee assignments to this project
        $sql = "DELETE FROM employee_projects WHERE project_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $project_id);
        $stmt->execute();
        
        // Delete manager assignments to this project
        $sql = "DELETE FROM manager_projects WHERE project_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $project_id);
        $stmt->execute();
        
        // Finally delete the project itself
        $sql = "DELETE FROM projects WHERE project_id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $project_id);
        $stmt->execute();
        
        // If we got here, everything worked, so commit the transaction
        $conn->commit();
        
        echo json_encode(["error" => false, "message" => "Project deleted successfully"]);
    } catch (Exception $e) {
        // If anything went wrong, rollback all changes
        $conn->rollback();
        echo json_encode(["error" => true, "message" => "Error deleting project: " . $e->getMessage()]);
    }
    
    $db->closeConnection();
} else {
    echo json_encode(["error" => true, "message" => "Invalid request method"]);
}