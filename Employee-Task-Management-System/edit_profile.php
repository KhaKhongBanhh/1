<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "employee") {
    include "DB_connection.php";
    include "app/Model/User.php";
    $user = get_user_by_id($conn, $_SESSION['id']);
    
    if (isset($_POST['update'])) {
        $full_name = $_POST['full_name'];
        $username = $_POST['username'];
        $old_username = $_POST['old_username'];
        $password = $_POST['password'];
        $c_password = $_POST['c_password']; // Thêm lại confirm password
        $old_password = $_POST['old_password'];
        $birthday = $_POST['birthday']; 
        
        // Kiểm tra nếu có thay đổi username
        if ($username != $old_username) {
            $query = "SELECT * FROM users WHERE username='$username'";
            $stmt = $conn->prepare($query);
            $stmt->execute();
            $count = $stmt->rowCount();
            
            if ($count > 0) {
                $em = "Tên đăng nhập đã tồn tại.";
                header("Location: edit_profile.php?error=$em");
                exit();
            }
        }
        
        // Kiểm tra mật khẩu
        if (!empty($password) && $password !== "**********") {
            // Kiểm tra confirm password
            if ($password !== $c_password) {
                $em = "Mật khẩu không khớp.";
                header("Location: edit_profile.php?error=$em");
                exit();
            }
            // Hash password mới
            $password = password_hash($password, PASSWORD_DEFAULT);
        } else {
            // Giữ nguyên password cũ
            $password = $old_password;
        }
        
        // Cập nhật thông tin cá nhân bao gồm cả birthday
        $sql = "UPDATE users SET 
                full_name=?, 
                username=?,
                password=?,
                birthday=?
                WHERE id=?";
        $stmt = $conn->prepare($sql);
        $stmt->execute([$full_name, $username, $password, $birthday, $_SESSION['id']]);
        
        $sm = "Cập nhật hồ sơ thành công!";
        header("Location: profile.php?success=$sm");
        exit();
    }
?>
<!DOCTYPE html>
<html>
<head>
    <title>Chỉnh sửa hồ sơ</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/edit_profile.css"> <!-- Đảm bảo đường dẫn chính xác -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title">
                <span><i class="fa fa-user-circle"></i> Chỉnh sửa hồ sơ</span>
                <a href="profile.php"><i class="fa fa-arrow-left"></i> Quay lại hồ sơ</a>
            </h4>
            
            <?php if (isset($_GET['error'])) { ?>
            <div class="danger" role="alert">
              <?php echo $_GET['error']; ?>
            </div>
            <?php } ?>
            
            <?php if (isset($_GET['success'])) { ?>
            <div class="success" role="alert">
              <?php echo $_GET['success']; ?>
            </div>
            <?php } ?>

            <form method="post" action="">
                <div class="form-section">
                    <label><i class="fa fa-user"></i> Họ tên</label>
                    <input type="text" name="full_name" value="<?=$user['full_name']?>">
                </div>
                
                <div class="form-section">
                    <label><i class="fa fa-id-badge"></i> Tên đăng nhập</label>
                    <input type="text" name="username" value="<?=$user['username']?>">
                </div>
                
                <div class="form-section">
                    <label><i class="fa fa-lock"></i> Mật khẩu mới</label>
                    <input type="password" name="password" value="**********" onfocus="if(this.value=='**********')this.value=''">
                </div>
                
                <div class="form-section">
                    <label><i class="fa fa-check-circle"></i> Xác nhận mật khẩu</label>
                    <input type="password" name="c_password" value="**********" onfocus="if(this.value=='**********')this.value=''">
                </div>
                
                <div class="form-section">
                    <label><i class="fa fa-calendar"></i> Ngày sinh</label>
                    <input type="date" name="birthday" value="<?php echo isset($user['birthday']) ? $user['birthday'] : ''; ?>">
                </div>
                
                <!-- Hidden inputs -->
                <input type="hidden" name="old_username" value="<?=$user['username']?>">
                <input type="hidden" name="old_password" value="<?=$user['password']?>">
                
                <button type="submit" name="update" class="btn"><i class="fa fa-save"></i> Cập nhật hồ sơ</button>
            </form>
        </section>
    </div>

<script type="text/javascript">
    var active = document.querySelector("#navList li:nth-child(3)");
    active.classList.add("active");
</script>
</body>
</html>
<?php } else { 
   $em = "Vui lòng đăng nhập trước";
   header("Location: login.php?error=$em");
   exit();
}
?>