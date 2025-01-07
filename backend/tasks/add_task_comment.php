<?php
require_once 'database.php';

if($_SERVER['REQUEST_METHOD'] == "POST") {
    $task_id = isset($_POST['task_id']) ? $_POST['task_id'] : "";
    $author_id = isset($_POST['author_id']) ? $_POST['author_id'] : "";
    $content = isset($_POST['content']) ? $_POST['content'] : "";
    $comment_id = uniqid();
    
    $db = new Database();
    $conn = $db->connect();
    
    $sql = "INSERT INTO task_comments (comment_id, task_id, author_id, content) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssss", $comment_id, $task_id, $author_id, $content);
    
    $response = array();
    if ($stmt->execute()) {
        $response['error'] = false;
        $response['message'] = "Comment added successfully!";
    } else {
        $response['error'] = true;
        $response['message'] = "Error: " . $conn->error;
    }
    
    echo json_encode($response);
    $db->closeConnection();
}
?>