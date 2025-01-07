<?php
require_once 'database.php';

if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST['notification_id'])) {
    $notification_id = $_POST['notification_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "UPDATE notifications SET is_archived = 1 WHERE notification_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $notification_id);
    
    $response = array();
    if($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Notification archived successfully";
    } else {
        $response['error'] = true;
        $response['message'] = "Error archiving notification: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>