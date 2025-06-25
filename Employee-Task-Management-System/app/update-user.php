<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {

if (isset($_POST['user_name']) && isset($_POST['password']) && isset($_POST['full_name']) && $_SESSION['role'] == 'admin') {
    include "../DB_connection.php";

    function validate_input($data) {
      $data = trim($data);
      $data = stripslashes($data);
      $data = htmlspecialchars($data);
      return $data;
    }

    $user_name = validate_input($_POST['user_name']);
    $password = validate_input($_POST['password']);
    $full_name = validate_input($_POST['full_name']);
    $id = validate_input($_POST['id']);


    if (empty($user_name)) {
        $em = "Tên đăng nhập không được để trống";
        header("Location: ../edit-user.php?error=$em&id=$id");
        exit();
    }else if (empty($password)) {
        $em = "Mật khẩu không được để trống";
        header("Location: ../edit-user.php?error=$em&id=$id");
        exit();
    }else if (empty($full_name)) {
        $em = "Họ tên không được để trống";
        header("Location: ../edit-user.php?error=$em&id=$id");
        exit();
    }else {
    
       include "Model/User.php";
       $password = password_hash($password, PASSWORD_DEFAULT);

       // Lấy thông tin người dùng để lấy vai trò hiện tại
       $user = get_user_by_id($conn, $id);
       $role = $user['role']; // Lấy vai trò hiện tại

       $data = array($full_name, $user_name, $password, $role, $id);
       update_user($conn, $data);

       $em = "Cập nhật người dùng thành công";
        header("Location: ../edit-user.php?success=$em&id=$id");
        exit();

    
    }
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../edit-user.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../edit-user.php?error=$em");
   exit();
}