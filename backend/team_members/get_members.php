// Retrieves all team members associated with a specific project
// Method: GET
// Parameters: project_id
// Returns: JSON array of team members with their user detail
<?php
require_once 'Database.php';

if(isset($_GET['project_id'])) {
    $database = new Database();
    $conn = $database->connect();
    
    $project_id = $conn->real_escape_string($_GET['project_id']);
    
    $sql = "SELECT e.*, u.* FROM employees e 
            JOIN users u ON e.user_id = u.user_id 
            WHERE e.project_id = '$project_id'";
    
    $result = $conn->query($sql);
    $resultarray = array();
    while($row = mysqli_fetch_assoc($result)) {
        $resultarray[] = $row;
    }
    
    echo json_encode($resultarray);
    $database->closeConnection();
}
?>