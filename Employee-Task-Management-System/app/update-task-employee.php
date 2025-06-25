<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {

if (isset($_POST['id']) && isset($_POST['status']) && $_SESSION['role'] == 'employee') {
	include "../DB_connection.php";

    function validate_input($data) {
	  $data = trim($data);
	  $data = stripslashes($data);
	  $data = htmlspecialchars($data);
	  return $data;
	}

	$status = validate_input($_POST['status']);
	$id = validate_input($_POST['id']);

    if (empty($status)) {
        $em = "Vui lòng chọn trạng thái";
        header("Location: ../edit-task-employee.php?error=$em&id=$id");
        exit();
    }else {
    
       include "Model/Task.php";

       $data = array($status, $id);
       update_task_status($conn, $data);

       $em = "Cập nhật công việc thành công";
        header("Location: ../edit-task-employee.php?success=$em&id=$id");
        exit();

    
    }
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../edit-task-employee.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../login.php?error=$em");
   exit();
}