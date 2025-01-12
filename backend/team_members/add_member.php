<?php
require_once '../config/database.php';

// Ensure we're receiving a POST request
if ($_SERVER['REQUEST_METHOD'] == "POST") {
    $database = new Database();
    $conn = $database->connect();
    
    // Get input parameters
    $project_id = $conn->real_escape_string($_POST['project_id']);
    $employee_id = $conn->real_escape_string($_POST['employee_id']);
    
    // Start transaction to ensure data consistency
    $conn->begin_transaction();
    
    try {
        // First verify the user is an employee and not already in the project
        $check_sql = "SELECT e.user_id 
                     FROM employees e
                     LEFT JOIN employee_projects ep 
                        ON e.user_id = ep.employee_id 
                        AND ep.project_id = ?
                     WHERE e.user_id = ? 
                     AND ep.employee_id IS NULL";
        
        $stmt = $conn->prepare($check_sql);
        $stmt->bind_param("ss", $project_id, $employee_id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 0) {
            throw new Exception("User is either not an employee or already in project");
        }
        
        // Add the employee to the project
        $insert_sql = "INSERT INTO employee_projects (project_id, employee_id) 
                      VALUES (?, ?)";
        
        $stmt = $conn->prepare($insert_sql);
        $stmt->bind_param("ss", $project_id, $employee_id);
        $stmt->execute();
        
        // If we got here, commit the transaction
        $conn->commit();
        
        $response = [
            'error' => false,
            'message' => "Member added successfully!"
        ];
        
    } catch (Exception $e) {
        // Something went wrong, rollback changes
        $conn->rollback();
        
        $response = [
            'error' => true,
            'message' => "Error: " . $e->getMessage()
        ];
    }
    
    echo json_encode($response);
    $database->closeConnection();
}
?>