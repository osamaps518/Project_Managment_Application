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
       
       // Get role if it exists (for employee registration)
       $role = isset($data["role"]) ? $conn->real_escape_string($data["role"]) : null;

       $conn->begin_transaction();
       try {
           // Insert into users table
           $insert_user = "INSERT INTO users (user_id, username, full_name, password, user_type) 
                          VALUES ('$user_id', '$name', '$name', '$password', '$user_type')";
           
           $conn->query($insert_user);

           // Insert into respective role table based on user type
           if ($user_type === 'manager') {
               $insert_role = "INSERT INTO project_managers (user_id) VALUES ('$user_id')";
               $conn->query($insert_role);
           } else if ($user_type === 'employee') {
               // Validate role exists for employee
               if (!$role) {
                   throw new Exception("Role is required for employee registration");
               }
               
               // Insert into employees table with role and default active status
               $insert_role = "INSERT INTO employees (user_id, role, status) 
                              VALUES ('$user_id', '$role', 'ACTIVE')";
               $conn->query($insert_role);
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