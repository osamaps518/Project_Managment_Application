<?php
// Removes a member from a project's team
// Method: POST
// Parameters: project_id, employee_id
// Returns: JSON response with success/error status and message

require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] == "POST") {
    $database = new Database();
    $conn = $database->connect();
    
    // Sanitize inputs
    $project_id = $conn->real_escape_string($_POST['project_id']);
    $employee_id = $conn->real_escape_string($_POST['employee_id']);
    
    // First verify if the employee exists and is active
    $verify_sql = "SELECT e.status FROM employees e WHERE e.user_id = '$employee_id'";
    $result = $conn->query($verify_sql);
    
    $response = array();
    
    if ($result && $result->num_rows > 0) {
        // Delete from employee_projects table
        $sql = "DELETE FROM employee_projects 
                WHERE project_id = '$project_id' 
                AND employee_id = '$employee_id'";
        
        if ($conn->query($sql) === TRUE) {
            if ($conn->affected_rows > 0) {
                // Also update any assigned tasks to unassigned
                $update_tasks = "UPDATE tasks 
                               SET assigned_to = NULL 
                               WHERE project_id = '$project_id' 
                               AND assigned_to = '$employee_id'";
                $conn->query($update_tasks);
                
                $response['error'] = false;
                $response['message'] = "Member removed successfully from project";
            } else {
                $response['error'] = true;
                $response['message'] = "Member was not found in this project";
            }
        } else {
            $response['error'] = true;
            $response['message'] = "Error removing member: " . $conn->error;
        }
    } else {
        $response['error'] = true;
        $response['message'] = "Employee not found or inactive";
    }
    
    echo json_encode($response);
    $database->closeConnection();
}
?>