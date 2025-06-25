<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {

if (isset($_POST['title']) && isset($_POST['description']) && isset($_POST['assigned_to']) && $_SESSION['role'] == 'admin' && isset($_POST['due_date'])) {
	include "../DB_connection.php";

    function validate_input($data) {
      $data = trim($data);
      $data = stripslashes($data);
      $data = htmlspecialchars($data);
      return $data;
    }

    $title = validate_input($_POST['title']);
    $description = validate_input($_POST['description']);
    $assigned_to_arr = $_POST['assigned_to'];
    $due_date = validate_input($_POST['due_date']);

    if (empty($title)) {
        $em = "Tiêu đề không được để trống";
        header("Location: ../create_task.php?error=$em");
        exit();
    }else if (empty($description)) {
        $em = "Mô tả không được để trống";
        header("Location: ../create_task.php?error=$em");
        exit();
    }else if (empty($assigned_to_arr)) {
        $em = "Vui lòng chọn người thực hiện";
        header("Location: ../create_task.php?error=$em");
        exit();
    }else {
    
       include "Model/Task.php";
       include "Model/Notification.php";

       foreach ($assigned_to_arr as $assigned_to) {
           $data = array($title, $description, $assigned_to, $due_date);
           insert_task($conn, $data);
       }

       $notif_data = array("'$title' đã được giao cho bạn. Vui lòng xem xét và bắt đầu thực hiện", $assigned_to, 'New Task Assigned');
       insert_notification($conn, $notif_data);


       $em = "Tạo công việc thành công";
        header("Location: ../create_task.php?success=$em");
        exit();

    
    }
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../create_task.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../create_task.php?error=$em");
   exit();
}