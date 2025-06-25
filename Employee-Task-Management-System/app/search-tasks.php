<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {
    include "../DB_connection.php";
    include "Model/Task.php";

    if(isset($_POST['search'])) {
        $search = $_POST['search'];
        $tasks = search_tasks($conn, $_SESSION['id'], $search);
        
        if($tasks != 0) {
            $i = 0;
            foreach($tasks as $task) {
                $i++;
                echo "<tr>
                    <td>".$i."</td>
                    <td>".$task['title']."</td>
                    <td>".$task['description']."</td>
                    <td>".$task['status']."</td>
                    <td>".$task['due_date']."</td>
                    <td>
                        <a href='../edit-task-employee.php?id=".$task['id']."' class='edit-btn'>Edit</a>
                    </td>
                </tr>";
            }
        } else {
            echo "<tr><td colspan='6' style='text-align: center;'>No tasks found</td></tr>";
        }
    }
} else {
    header("Location: ../login.php");
    exit();
}
?> 