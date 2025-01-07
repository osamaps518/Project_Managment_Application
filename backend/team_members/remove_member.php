<?php
require_once 'Database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
   $database = new Database();
   $conn = $database->connect();
   
   $project_id = $conn->real_escape_string($_POST['project_id']);
   $employee_id = $conn->real_escape_string($_POST['employee_id']);
   
   $sql = "DELETE FROM project_members WHERE project_id='$project_id' AND user_id='$employee_id'";
   
   $response = array();
   if ($conn->query($sql) === TRUE) {
       $response['error'] = false;
       $response['message'] = "Member removed successfully";
   } else {
       $response['error'] = true;
       $response['message'] = "Error: " . $conn->error;
   }
   
   echo json_encode($response);
   $database->closeConnection();
}
?>