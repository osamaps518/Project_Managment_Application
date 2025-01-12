<?php
require_once '../config/database.php';

if(isset($_GET['manager_id']) && isset($_GET['query'])) {
   $manager_id = $_GET['manager_id'];
   $query = '%' . $_GET['query'] . '%';
   
   $db = new Database();
   $conn = $db->connect();
   
   // Updated query to use new schema's structure and include more search fields
   $sql = "SELECT t.*, u.full_name as assigned_name, u.username,
           p.title as project_title 
           FROM tasks t
           JOIN projects p ON t.project_id = p.project_id 
           JOIN manager_projects mp ON p.project_id = mp.project_id
           LEFT JOIN users u ON t.assigned_to = u.user_id 
           WHERE mp.manager_id = ? 
           AND (t.title LIKE ? OR t.description LIKE ? OR u.full_name LIKE ?)
           ORDER BY t.due_date ASC";
           
   $stmt = $conn->prepare($sql);
   $stmt->bind_param("ssss", $manager_id, $query, $query, $query);
   $stmt->execute();
   $result = $stmt->get_result();
   
   $tasks = array();
   while($row = $result->fetch_assoc()) {
       $tasks[] = $row;
   }
   
   echo json_encode($tasks);
   $db->closeConnection();
}
?>