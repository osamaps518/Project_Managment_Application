<?php
require_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
   $sender_id = $_POST['sender_id'];
   $project_id = $_POST['project_id']; 
   $receiver_id = $_POST['receiver_id']; 
   $title = $_POST['title'];
   $content = $_POST['content'];
   $type = $_POST['type'];

   $db = new Database();
   $conn = $db->connect();

   // First verify both users are in the project
   $verify_sql = "SELECT COUNT(*) as count FROM project_members 
                  WHERE project_id = ? AND user_id IN (?, ?)";
   $stmt = $conn->prepare($verify_sql);
   $stmt->bind_param("sss", $project_id, $sender_id, $receiver_id);
   $stmt->execute();
   $result = $stmt->get_result();
   $count = $result->fetch_assoc()['count'];

   if ($count == 2) { // Both users are in the project
       $sql = "INSERT INTO notifications (notification_id, type, sender_id, receiver_id, title, content, timestamp)
               VALUES (UUID(), ?, ?, ?, ?, ?, NOW())";
       
       $stmt = $conn->prepare($sql);
       $stmt->bind_param("sssss", $type, $sender_id, $receiver_id, $title, $content);
       
       if ($stmt->execute()) {
           echo json_encode(["error" => false]);
       } else {
           echo json_encode(["error" => true, "message" => "Failed to send message"]);
       }
   } else {
       echo json_encode(["error" => true, "message" => "Invalid sender or receiver for this project"]);
   }

   $db->closeConnection();
}
?>