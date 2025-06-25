<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {

if (isset($_POST['confirm_password']) && isset($_POST['new_password']) && isset($_POST['password']) && isset($_POST['full_name']) && $_SESSION['role'] == 'employee') {
    include "../DB_connection.php";

    function validate_input($data) {
      $data = trim($data);
      $data = stripslashes($data);
      $data = htmlspecialchars($data);
      return $data;
    }

	
	$password = validate_input($_POST['password']);
	$full_name = validate_input($_POST['full_name']);
	$new_password = validate_input($_POST['new_password']);
	$confirm_password = validate_input($_POST['confirm_password']);
   $id = $_SESSION['id'];

    if (empty($password) || empty($new_password) || empty($confirm_password) ) {
        $em = "Mật khẩu không được để trống";
        header("Location: ../edit_profile.php?error=$em");
        exit();
    }else if (empty($full_name)) {
        $em = "Họ tên không được để trống";
        header("Location: ../edit_profile.php?error=$em");
        exit();
    }else if ($confirm_password != $new_password) {
        $em = "Mật khẩu mới và xác nhận mật khẩu không khớp";
        header("Location: ../edit_profile.php?error=$em");
        exit();
    }else {
    
       include "Model/User.php";

       $user = get_user_by_id($conn, $id);
       if ($user) {
       	 if (password_verify($password, $user['password'])) {

			       $new_password = password_hash($new_password, PASSWORD_DEFAULT);



			       $data = array($full_name, $new_password, $id);
			       update_profile($conn, $data);

                   $em = "Cập nhật thông tin thành công";
                    header("Location: ../edit_profile.php?success=$em");
                    exit();
                }else {
                	$em = "Mật khẩu không chính xác";
                   header("Location: ../edit_profile.php?error=$em");
                   exit();
                }
           }else {
            $em = "Đã xảy ra lỗi không xác định";
               header("Location: ../edit_profile.php?error=$em");
               exit();
           }

    
	}
}else {
   $em = "Đã xảy ra lỗi không xác định";
   header("Location: ../edit_profile.php?error=$em");
   exit();
}

}else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: ../login.php?error=$em");
   exit();
}