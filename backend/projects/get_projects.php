<?php
require_once '../config/database.php';

$database = new Database();
$conn = $database->connect();

$manager_id = $conn->real_escape_string($_GET['manager_id']);
$sql = "SELECT p.* FROM projects p 
        JOIN manager_projects mp ON p.project_id = mp.project_id 
        WHERE mp.manager_id = '$manager_id'";

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