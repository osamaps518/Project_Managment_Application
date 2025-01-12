<?php
require_once '../config/database.php';

$database = new Database();
$conn = $database->connect();

// Check which parameter is provided
$manager_id = isset($_GET['manager_id']) ? $conn->real_escape_string($_GET['manager_id']) : null;
$employee_id = isset($_GET['employee_id']) ? $conn->real_escape_string($_GET['employee_id']) : null;

// Build the appropriate query based on the provided ID
if ($manager_id) {
    $sql = "SELECT p.* FROM projects p 
            JOIN manager_projects mp ON p.project_id = mp.project_id 
            WHERE mp.manager_id = '$manager_id'";
} else if ($employee_id) {
    $sql = "SELECT p.* FROM projects p 
            JOIN employee_projects ep ON p.project_id = ep.project_id 
            WHERE ep.employee_id = '$employee_id'";
} else {
    echo json_encode(array("error" => true, "message" => "No valid ID provided"));
    $database->closeConnection();
    exit;
}

$result = $conn->query($sql);

$projects = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $projects[] = $row;
    }
}

echo json_encode($projects);

$database->closeConnection();
?>