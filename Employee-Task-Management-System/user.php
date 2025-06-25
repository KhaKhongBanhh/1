<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "admin") {
    include "DB_connection.php";
    include "app/Model/User.php";

    // Nếu có dữu liệu gửi từ ajax thì xử lý
    if (isset($_POST['ajax_search_user'])) {
        $search = trim($_POST['ajax_search_user']);
        // Sử dụng hàm search_users để tìm kiếm người dùng trong file User.php
        $users = search_users($conn, $search);
        // Nếu có người dùng thì hiển thị
        if($users != 0) {
            $i = 0;
            foreach($users as $user) {
                $i++;
                // Chuyển đổi role sang tiếng Việt khi hiển thị
                $roleText = ($user['role'] == 'admin') ? 'Quản trị viên' : 'Nhân viên';
                echo "<tr>
                    <td>".$i."</td>
                    <td>".$user['full_name']."</td>
                    <td>".$user['username']."</td>
                    <td>".($user['birthday'] ? htmlspecialchars($user['birthday']) : '')."</td>
                    <td>".$roleText."</td>
                    <td>
                        <a href='edit-user.php?id=".$user['id']."' class='edit-btn'>Sửa</a> ";
                if(!($user['role'] == 'admin' && $user['username'] == 'admin')) {
                    echo "<a href='delete-user.php?id=".$user['id']."' class='delete-btn' onclick='return confirm(\"Bạn có chắc chắn không?\")'>Xóa</a>";
                }
                echo "</td>
                </tr>";
            }
        } else {
            echo "<tr><td colspan='6' style='text-align: center;'>Không tìm thấy người dùng nào</td></tr>";
        }
        exit();
    }

    // Nếu không có tìm kiếm thì lấy tất cả người dùng
    if (isset($_GET['search']) && !empty($_GET['search'])) {
        $users = search_users($conn, $_GET['search']);
    } else {
        $users = get_all_users($conn);
    }
  
 ?>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý Người dùng</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/user.css">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title">Quản lý Người dùng <a href="add-user.php">Thêm người dùng</a></h4>
            <?php if (isset($_GET['success'])) {?>
            	<div class="success" role="alert">
              <?php echo stripcslashes($_GET['success']); ?>
            </div>
        <?php } ?>
            <!-- Thêm form tìm kiếm bằng ajax-->
            <form action="" method="GET" class="search-form">
            <!-- Nếu người dùng nhập tìm kiếm tức là tồn tại biến GET search trên URL -->
            <!-- Giá trị này được hiển thị lại trong ô input khi tải lại trang -->
                <input type="text" name="search" id="searchInput" placeholder="Tìm kiếm theo Họ tên, Tên đăng nhập hoặc Vai trò"
                    value="<?php if(isset($_GET['search'])) echo htmlspecialchars($_GET['search']); ?>">
                <button type="submit" class="search-btn">Tìm kiếm</button>
                <a href="user.php" class="clear-btn">Xóa</a>
            </form>

            <?php if ($users != 0) { ?>
            <table class="main-table">
                <tr>
                    <th>#</th>
                    <th>Họ tên</th>
                    <th>Tên đăng nhập</th>
                    <th>Ngày sinh</th> <!-- Thêm cột Birthday -->
                    <th>Vai trò</th>
                    <th>Hành động</th>
                </tr>
                <tbody id="userTableBody">
                <?php $i=0; foreach ($users as $user) { 
                    // Chuyển đổi role sang tiếng Việt khi hiển thị
                    $roleText = ($user['role'] == 'admin') ? 'Quản trị viên' : 'Nhân viên';
                ?>
                <tr>
                    <td><?=++$i?></td>
                    <td><?=$user['full_name']?></td>
                    <td><?=$user['username']?></td>
                    <td><?=isset($user['birthday']) ? htmlspecialchars($user['birthday']) : ''?></td> <!-- Hiển thị ngày sinh -->
                    <td><?=$roleText?></td>
                    <td>
                        <a href="edit-user.php?id=<?=$user['id']?>" class="edit-btn">Sửa</a>
                        <?php if (!($user['role'] == "admin" && $user['username'] == "admin")) { ?>
                            <a href="delete-user.php?id=<?=$user['id']?>" class="delete-btn">Xóa</a>
                        <?php } ?>
                    </td>
                </tr>
               <?php	} ?>
                </tbody>
            </table>
        <?php }else { ?>
            <h3>Không có dữ liệu</h3>
        <?php  }?>
            
        </section>
    </div>

<script type="text/javascript">
    // 
    var active = document.querySelector("#navList li:nth-child(2)");
    active.classList.add("active");

    // Đầu tiên khi người dùng nhập ô tìm kiếm
    // Lấy giá trị trong ô searchInput
    document.getElementById('searchInput').addEventListener('keyup', function() {
        // Mỗi lần gõ hàm sẽ lấy giá trị vừa nhập
        var search = this.value;
        // Tạo một request AJAX và gửi đến file user.php với phương thức POST
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'user.php', true);
        // Đặt header cho request để gửi dữ liệu dưới dạng form
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = function() {
            // Nếu trạng thái của request là 4 (hoàn thành) và mã trạng thái là 200 (thành công)
            // Thì cập nhật bảng người dùng
            if(xhr.readyState == 4 && xhr.status == 200) {
                document.getElementById('userTableBody').innerHTML = xhr.responseText;
            }
        }
        // Gửi request với dữ liệu là giá trị tìm kiếm
        // Bên server sẽ nhận giá trị này qua biến $_POST['ajax_search_user']
        xhr.send('ajax_search_user=' + encodeURIComponent(search));
    });
</script>
</body>
</html>
<?php }else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: login.php?error=$em");
   exit();
}
 ?>