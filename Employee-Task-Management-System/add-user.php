<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "admin") {
    include "DB_connection.php";
    include "app/Model/User.php";

    // Xử lý form submit
    if (isset($_POST['submit'])) {
        // Xử lý và kiểm tra dữ liệu
        $full_name = $_POST['full_name'];
        $username = $_POST['username'];
        $role = $_POST['role'];
        $password = $_POST['password']; 
        $password_confirm = $_POST['password_confirm'];
        $birthday = $_POST['birthday']; // Thêm dòng này để lấy giá trị birthday
        
        // Kiểm tra các điều kiện...
        $valid = true; // Giả sử dữ liệu hợp lệ
        
        // Nếu dữ liệu hợp lệ
        if ($valid) {
            // Hash mật khẩu
            $password = password_hash($password, PASSWORD_DEFAULT);
            
            // Tạo mảng data để thêm user mới
            $data = [
                $full_name,
                $username,
                $password,
                $role,
                $birthday // Thêm giá trị birthday vào mảng data
            ];
            
            // Gọi hàm insert_user
            insert_user($conn, $data);
            
            // Chuyển hướng sau khi thêm thành công
            header("Location: user.php?success=Tạo người dùng thành công");
            exit;
        }
    }
?>
<!DOCTYPE html>
<html>
<head>
    <title>Thêm người dùng</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/add-user.css">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title">Thêm người dùng <a href="user.php">Người dùng</a></h4>
            <form class="form-1"
                  method="POST"
                  action="">
                  <?php if (isset($_GET['error'])) {?>
            	<div class="danger" role="alert">
              <?php echo stripcslashes($_GET['error']); ?>
            </div>
            <?php } ?>

            <?php if (isset($_GET['success'])) {?>
            	<div class="success" role="alert">
              <?php echo stripcslashes($_GET['success']); ?>
            </div>
            <?php } ?>
                <div class="input-holder">
                    <lable>Họ tên</lable>
                    <input type="text" name="full_name" class="input-1" placeholder="Họ tên"><br>
                </div>
                <div class="input-holder">
                    <lable>Tên đăng nhập</lable>
                    <input type="text" name="username" class="input-1" placeholder="Tên đăng nhập" onchange="validateUsername(this.value)"><br>
                </div>
                <div class="input-holder">
                    <lable>Mật khẩu</lable>
                    <input type="text" name="password" class="input-1" placeholder="Mật khẩu"><br>
                </div>
                <div class="input-holder">
                    <lable>Vai trò</lable>
                    <select name="role" class="input-1">
                        <option value="employee">Nhân viên</option>
                        <option value="admin">Quản trị viên</option>
                    </select><br>
                </div>
                <div class="form-group">
    <label>Ngày sinh</label>
    <input type="date" name="birthday" class="input-1">
</div>

                <button class="edit-btn" name="submit">Thêm</button>
            </form>
            
        </section>
    </div>

<script type="text/javascript">
    var active = document.querySelector("#navList li:nth-child(2)");
    active.classList.add("active");
    
    // Hàm validate dữ liệu cho admin
    function validateUsername(username) {
        if (username.toLowerCase() === "admin") {
            alert("Tên đăng nhập 'admin' không được phép sử dụng");
            document.querySelector('input[name="user_name"]').value = "";
        }
    }
</script>
</body>
</html>
<?php 
} else { 
    $em = "Vui lòng đăng nhập trước";
    header("Location: login.php?error=$em");
    exit();
}
?>