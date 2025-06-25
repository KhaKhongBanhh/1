<?php
// Đảm bảo file này chỉ chứa mã PHP
include_once "app/Model/Notification.php";

// Kiểm tra sinh nhật mỗi lần trang được tải 
// hàm này sẽ chuyển đến trang Notification.php
if (isset($conn) && function_exists('check_birthdays')) {
    check_birthdays($conn);
}
?>