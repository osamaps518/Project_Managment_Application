<?php
if(isset($_GET['project_id']) && isset($_GET['role'])) {
    $project_id = $_GET['project_id'];
    $role = $_GET['role'];
    
    $conn = new mysqli($server_name, $username, $password, $dbname);
    
    $sql = "SELECT e.*, u.* FROM employees e 
            JOIN users u ON e.user_id = u.user_id 
            WHERE e.project_id = '" . $project_id . "' 
            AND e.role = '" . $role . "'";
    
    $result = $conn->query($sql);
    $resultarray = array();
    while($row = mysqli_fetch_assoc($result)) {
        $resultarray[] = $row;
    }
    echo json_encode($resultarray);
    $conn->close();
}
?>
