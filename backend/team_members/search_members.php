// Searches for team members by name or username within a project
// Method: GET
// Parameters: project_id, query (search string)
// Returns: JSON array of matching team members
<?php
require_once 'Database.php';

if(isset($_GET['project_id']) && isset($_GET['query'])) {
   $database = new Database();
   $conn = $database->connect();
   
   $project_id = $conn->real_escape_string($_GET['project_id']);
   $query = $conn->real_escape_string($_GET['query']);
   
   $sql = "SELECT u.* FROM users u 
           JOIN project_members pm ON u.user_id = pm.user_id 
           WHERE pm.project_id = '$project_id' 
           AND (u.full_name LIKE '%$query%' OR u.username LIKE '%$query%')";
   
   $result = $conn->query($sql);
   $resultarray = array();
   while($row = mysqli_fetch_assoc($result)) {
       $resultarray[] = $row;
   }
   
   echo json_encode($resultarray);
   $database->closeConnection();
}
?>
