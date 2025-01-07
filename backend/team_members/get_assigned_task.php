// Retrieves the current active task assigned to a team member
// Method: GET
// Parameters: project_id, employee_id
// Returns: JSON object with task details or null if no active task

<?php
require_once 'Database.php';

if(isset($_GET['project_id']) && isset($_GET['employee_id'])) {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    $employee_id = $conn->real_escape_string($_GET['employee_id']);
    
    $sql = "SELECT * FROM tasks 
            WHERE project_id = '$project_id' 
            AND assigned_to = '$employee_id' 
            AND status != 'COMPLETED'
            LIMIT 1";
    
    $result = $conn->query($sql);
    
    if($row = mysqli_fetch_assoc($result)) {
        echo json_encode($row);
    } else {
        echo json_encode(null);
    }
    
    $database->closeConnection();
}
?>