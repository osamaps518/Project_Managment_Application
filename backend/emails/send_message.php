<?php
require_once 'database.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
   $sender_id = $_POST['sender_id'];
   $project_id = $_POST['project_id']; 
   $title = $_POST['title'];
   $content = $_POST['content'];
   $type = $_POST['type'];

   $db = new Database();
   $conn = $db->connect();

   // Get project members to send notification to
   $sql = "INSERT INTO notifications (notification_id, type, sender_id, title, content, timestamp)
           VALUES (UUID(), ?, ?, ?, ?, UNIX_TIMESTAMP())";

   $stmt = $conn->prepare($sql);
   $stmt->bind_param("ssss", $type, $sender_id, $title, $content);
   
   if ($stmt->execute()) {
       echo json_encode(["error" => false]);
   } else {
       echo json_encode(["error" => true, "message" => "Failed to send message"]);
   }

   $db->closeConnection();
}
?>