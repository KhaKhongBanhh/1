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
    $role = isset($_POST['role']) ? validate_input($_POST['role']) : "employee";

    if (empty($user_name)) {
        $em = "Tên đăng nhập không được để trống";
        header("Location: ../add-user.php?error=$em");
        exit();
    }else if (empty($password)) {
        $em = "Mật khẩu không được để trống";
        header("Location: ../add-user.php?error=$em");
        exit();
    }else if (empty($full_name)) {
        $em = "Họ tên không được để trống";
        header("Location: ../add-user.php?error=$em");
        exit();
    }else if (strtolower($user_name) === "admin") {
        $em = "Tên đăng nhập 'admin' không được phép sử dụng";
        header("Location: ../add-user.php?error=$em");
        exit();
    }else {
    
       include "Model/User.php";
       
       // Kiểm tra username đã tồn tại chưa
       if(check_username($conn, $user_name)) {
           $em = "Tên đăng nhập đã tồn tại";
           header("Location: ../add-user.php?error=$em");
           exit();
       }

       $password = password_hash($password, PASSWORD_DEFAULT);

       $data = array($full_name, $user_name, $password, $role);
       insert_user($conn, $data);

       $em = "Tạo người dùng thành công";
        header("Location: ../add-user.php?success=$em");
        exit();

    
    }
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../add-user.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../add-user.php?error=$em");
   exit();
}