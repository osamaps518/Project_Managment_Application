<?php
require_once '../config/database.php';

$database = new Database();
$conn = $database->connect();

$data = json_decode(file_get_contents("php://input"), true);

if ($_SERVER["REQUEST_METHOD"] == "POST") {
   if (isset($data["Name"]) && isset($data["password"]) && isset($data["type"])) {
       $user_id = bin2hex(random_bytes(16)); // Generate UUID
       $name = $conn->real_escape_string($data["Name"]);
       $password = $conn->real_escape_string($data["password"]);
       $user_type = $conn->real_escape_string($data["type"]);

       $conn->begin_transaction();
       try {
           $insert_user = "INSERT INTO users (user_id, username, full_name, password, user_type) 
                          VALUES ('$user_id', '$name', '$name', '$password', '$user_type')";
           
           $conn->query($insert_user);

           if ($user_type === 'manager') {
               $insert_manager = "INSERT INTO project_managers (user_id) VALUES ('$user_id')";
               $conn->query($insert_manager);
           }

           $conn->commit();
           echo json_encode(array("success" => true, "message" => "Registration successful."));
       } catch (Exception $e) {
           $conn->rollback();
           echo json_encode(array("success" => false, "message" => "Error: " . $e->getMessage()));
       }
   } else {
       echo json_encode(array("success" => false, "message" => "All fields are required."));
   }
} else {
   echo json_encode(array("success" => false, "message" => "Invalid request method."));
}

$database->closeConnection();
?>