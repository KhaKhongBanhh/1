<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {

if (isset($_POST['id']) && isset($_POST['title']) && isset($_POST['description']) && isset($_POST['assigned_to']) && $_SESSION['role'] == 'admin'&& isset($_POST['due_date'])) {
    include "../DB_connection.php";

    function validate_input($data) {
      $data = trim($data);
      $data = stripslashes($data);
      $data = htmlspecialchars($data);
      return $data;
    }

    $title = validate_input($_POST['title']);
    $description = validate_input($_POST['description']);
    $assigned_to = validate_input($_POST['assigned_to']);
    $id = validate_input($_POST['id']);
    $due_date = validate_input($_POST['due_date']);

    if (empty($title)) {
        $em = "Tiêu đề không được để trống";
        header("Location: ../edit-task.php?error=$em&id=$id");
        exit();
    }else if (empty($description)) {
        $em = "Mô tả không được để trống";
        header("Location: ../edit-task.php?error=$em&id=$id");
        exit();
    }else if ($assigned_to == 0) {
        $em = "Vui lòng chọn người thực hiện";
        header("Location: ../edit-task.php?error=$em&id=$id");
        exit();
    }else {
    
       include "Model/Task.php";

       // Kiểm tra trùng task
       if (is_duplicate_task($conn, $title, $description, $due_date, $assigned_to, $id)) {
           $em = "Công việc trùng với một công việc đã có (cùng tiêu đề, mô tả, hạn và người được giao)";
           header("Location: ../edit-task.php?error=$em&id=$id");
           exit();
       }

       $data = array($title, $description, $assigned_to, $due_date, $id);
       update_task($conn, $data);

       $em = "Cập nhật công việc thành công";
        header("Location: ../edit-task.php?success=$em&id=$id");
        exit();

    
    }
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../edit-task.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../login.php?error=$em");
   exit();
}