<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "admin") {
    include "DB_connection.php";
    include "app/Model/User.php";
    
    if (!isset($_GET['id'])) {
         header("Location: user.php");
         exit();
    }
    $id = $_GET['id'];
    $user = get_user_by_id($conn, $id);

    if ($user == 0) {
         header("Location: user.php");
         exit();
    }

    // Kiểm tra nếu là tài khoản admin chính thì không cho xóa
    if ($user['role'] == "admin" && $user['username'] == "admin") {
        $em = "Không thể xóa tài khoản admin chính";
        header("Location: user.php?error=$em");
        exit();
    }

    $data = array($id);
    delete_user($conn, $data);
    $sm = "Xóa người dùng thành công";
    header("Location: user.php?success=$sm");
    exit();

 }else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: login.php?error=$em");
   exit();
}
 ?>