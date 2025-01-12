<?php
require_once 'database.php';

if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST['notification_id'])) {
    $notification_id = $_POST['notification_id'];
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "DELETE FROM notifications WHERE notification_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $notification_id);
    
    $response = array();
    if($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Notification removed successfully";
    } else {
        $response['error'] = true;
        $response['message'] = "Error removing notification: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>