<?php
// Adds a new member to a project's team
// Method: POST
// Parameters: project_id, employee_id
// Returns: JSON response with success/error status and message


require_once '../config/database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_POST['project_id']);
    $user_id = $conn->real_escape_string($_POST['user_id']);
    
    $sql = "INSERT INTO project_members (project_id, user_id, role) 
        VALUES ('$project_id', '$user_id', 'Developer')";
    
    $response = array();
    if ($conn->query($sql) === TRUE) {
        $response['error'] = false;
        $response['message'] = "Member added successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Error: " . $conn->error;
    }
    
    echo json_encode($response);
    $database->closeConnection();
}
?>