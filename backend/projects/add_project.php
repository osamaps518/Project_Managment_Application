<?php
require_once '../config/database.php';

$database = new Database();
$conn = $database->connect();

$manager_id = $conn->real_escape_string($_POST['manager_id']);
$title = $conn->real_escape_string($_POST['title']);
$description = $conn->real_escape_string($_POST['description']);
$start_date = $conn->real_escape_string($_POST['start_date']);
$due_date = $conn->real_escape_string($_POST['due_date']);
$status = 'Planning';

$project_id = uniqid();

$conn->begin_transaction();
try {
    $sql1 = "INSERT INTO projects (project_id, title, description, status, start_date, due_date) 
            VALUES ('$project_id', '$title', '$description', '$status', '$start_date', '$due_date')";
    
    $sql2 = "INSERT INTO manager_projects (manager_id, project_id) 
            VALUES ('$manager_id', '$project_id')";

    $conn->query($sql1);
    $conn->query($sql2);
    $conn->commit();
    
    echo json_encode(["status" => "success", "message" => "Project added successfully"]);
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["status" => "error", "message" => "Error: " . $e->getMessage()]);
}

$database->closeConnection();
?>