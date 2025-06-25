<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "admin") {
    include "DB_connection.php";
    include "app/Model/User.php";

    // Lấy thông tin người dùng cần sửa
    $id = $_GET['id'];
    $user = get_user_by_id($conn, $id);
    
    // Xử lý form update
    if (isset($_POST['submit'])) {
        // Lấy dữ liệu từ form
        $id = $_POST['id'];
        $full_name = $_POST['full_name'];
        $username = $_POST['username'];
        $role = $_POST['role'];
        $birthday = $_POST['birthday']; // Thêm dòng này để lấy giá trị birthday
        
        // Kiểm tra mật khẩu
        if (!empty($_POST['password']) && $_POST['password'] !== '**********') {
            $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
        } else {
            $password = $user['password']; // Giữ nguyên mật khẩu cũ
        }
        
        // Tạo mảng data để cập nhật
        $data = [
            $full_name,
            $username,
            $password,
            $role,
            $birthday, // Thêm birthday vào mảng data
            $id
        ];
        
        // Gọi hàm update_user
        update_user($conn, $data);
        
        // Chuyển hướng sau khi cập nhật thành công
        header("Location: user.php?success=Cập nhật người dùng thành công");
        exit;
    }
?>
<!DOCTYPE html>
<html>
<head>
    <title>Chỉnh sửa người dùng</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/edit-user.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title">
                <span>Chỉnh sửa người dùng</span> 
                <a href="user.php"><i class="fa fa-users"></i> Tất cả người dùng</a>
            </h4>
            
            <?php if (isset($_GET['error'])) { ?>
                <div class="danger" role="alert">
                    <?php echo htmlspecialchars($_GET['error']); ?>
                </div>
            <?php } ?>
            
            <?php if (isset($_GET['success'])) { ?>
                <div class="success" role="alert">
                    <?php echo htmlspecialchars($_GET['success']); ?>
                </div>
            <?php } ?>
            
            <form class="form-1" method="POST" action="">
                <div class="input-holder">
                    <lable>Họ tên</lable>
                    <input type="text" name="full_name" class="input-1" placeholder="Họ tên" value="<?=$user['full_name']?>">
                </div>
                
                <div class="input-holder">
                    <lable>Tên đăng nhập</lable>
                    <input type="text" name="username" value="<?=$user['username']?>" class="input-1" placeholder="Tên đăng nhập">
                </div>
                
                <div class="input-holder">
                    <lable>Mật khẩu (giữ nguyên để không thay đổi)</lable>
                    <input type="text" value="**********" name="password" class="input-1" placeholder="Mật khẩu">
                </div>
                
                <div class="input-holder">
                    <lable>Vai trò</lable>
                    <?php if ($user['username'] == 'admin' && $user['role'] == 'admin') { ?>
                        <!-- Nếu là tài khoản admin chính thức, không cho phép thay đổi role -->
                        <input type="text" class="input-1" value="<?=$user['role']?>" readonly>
                        <input type="hidden" name="role" value="<?=$user['role']?>">
                        <small class="text-muted">Vai trò của tài khoản Admin chính không thể thay đổi</small>
                    <?php } else { ?>
                        <!-- Các tài khoản khác có thể thay đổi role -->
                        <select name="role" class="input-1">
                            <option value="admin" <?=$user['role'] == 'admin' ? 'selected' : ''?>>Quản trị viên</option>
                            <option value="employee" <?=$user['role'] == 'employee' ? 'selected' : ''?>>Nhân viên</option>
                        </select>
                    <?php } ?>
                </div>
                
                <div class="input-holder">
                    <lable>Ngày sinh</lable>
                    <input type="date" name="birthday" class="input-1" value="<?php echo isset($user['birthday']) ? htmlspecialchars($user['birthday']) : ''; ?>">
                </div>
                
                <input type="hidden" name="id" value="<?=$user['id']?>">
                <button class="edit-btn" name="submit"><i class="fa fa-save"></i> Cập nhật người dùng</button>
            </form>
        </section>
    </div>

<script type="text/javascript">
    // Đánh dấu menu user là active
    var active = document.querySelector("#navList li:nth-child(2)");
    active.classList.add("active");
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