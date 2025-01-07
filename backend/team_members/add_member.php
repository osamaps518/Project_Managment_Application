<?php
require_once 'Database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_POST['project_id']);
    $employee_id = $conn->real_escape_string($_POST['employee_id']);
    
    $sql = "INSERT INTO project_members (project_id, employee_id) VALUES ('$project_id', '$employee_id')";
    
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