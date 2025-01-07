<?php
require_once 'Database.php';

if(isset($_GET['user_id'])) {
   $database = new Database();
   $conn = $database->connect();
   
   $user_id = $conn->real_escape_string($_GET['user_id']);
   
   $sql = "SELECT * FROM users WHERE user_id = '$user_id'";
   
   $result = $conn->query($sql);
   if($row = mysqli_fetch_assoc($result)) {
       echo json_encode($row);
   } else {
       $response = array('error' => true, 'message' => 'Employee not found');
       echo json_encode($response);
   }
   
   $database->closeConnection();
}
?>
