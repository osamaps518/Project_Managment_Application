<?php
require_once '../config/database.php';

$database = new Database();
$conn = $database->connect();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['Name']) && isset($_POST['password'])) {
        $input_username = $conn->real_escape_string($_POST['Name']);
        $input_password = $conn->real_escape_string($_POST['password']);
        
        $sql = "SELECT * FROM users WHERE username = '$input_username' AND password = '$input_password'";
        $result = $conn->query($sql);

        if ($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            echo json_encode([
               "status2" => "success", 
               "type" => $user['user_type'],
               "user_id" => $user['user_id']
            ]);
        } else {
            echo json_encode(["status3" => "error", "message" => "Invalid credentials"]);
        }
    } else {
        echo json_encode(["status4" => "error", "message" => "Missing username or password"]);
    }
} else {
    echo json_encode(["status5" => "error", "message" => "Invalid request method"]);
}

$database->closeConnection();
?>