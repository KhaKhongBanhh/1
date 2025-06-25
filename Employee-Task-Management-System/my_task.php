<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id'])) {
    include "DB_connection.php";
    include "app/Model/Task.php";
    include "app/Model/User.php";

    $tasks = get_all_tasks_by_id($conn, $_SESSION['id']);

 ?>
<!DOCTYPE html>
<html>
<head>
    <title>Công việc của tôi</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/my_task.css">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title">Công việc của tôi</h4>
            <?php if (isset($_GET['success'])) {?>
            	<div class="success" role="alert">
              <?php echo stripcslashes($_GET['success']); ?>
            </div>
        <?php } ?>

            <!-- Thêm form tìm kiếm -->
            <form action="" method="GET" class="search-form">
                <input type="text" name="search" id="searchInput" placeholder="Tìm kiếm theo tiêu đề hoặc mô tả" 
                    value="<?php if(isset($_GET['search'])) echo $_GET['search']; ?>">
                <button type="submit" class="search-btn">Tìm kiếm</button>
                <a href="my_task.php" class="clear-btn">Xóa</a>
            </form>

            <?php 
            // Xử lý tìm kiếm
            if(isset($_GET['search']) && !empty($_GET['search'])) {
                $tasks = search_tasks($conn, $_SESSION['id'], $_GET['search']);
            } else {
                $tasks = get_all_tasks_by_id($conn, $_SESSION['id']);
            }
            ?>

            <?php if ($tasks != 0) { ?>
            <table class="main-table">
                <tr>
                    <th>#</th>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Trạng thái</th>
                    <th>Hạn hoàn thành</th>
                    <th>Hành động</th>
                </tr>
                <tbody id="taskTableBody">
                <?php $i=0; foreach ($tasks as $task) { 
                    // Việt hóa trạng thái công việc
                    $statusText = $task['status'];
                    if ($statusText == 'pending') {
                        $statusText = 'Đang chờ';
                    } else if ($statusText == 'in_progress') {
                        $statusText = 'Đang thực hiện';
                    } else if ($statusText == 'completed') {
                        $statusText = 'Đã hoàn thành';
                    }
                ?>
                <tr>
                    <td><?=++$i?></td>
                    <td><?=$task['title']?></td>
                    <td><?=$task['description']?></td>
                    <td><?=$statusText?></td>
                    <td><?=$task['due_date']?></td>
                    <td>
                        <a href="edit-task-employee.php?id=<?=$task['id']?>" class="edit-btn">Sửa</a>
                    </td>
                </tr>
               <?php	} ?>
                </tbody>
            </table>
        <?php }else { ?>
            <h3>Không có công việc</h3>
        <?php  }?>
            
        </section>
    </div>

<script type="text/javascript">
    var active = document.querySelector("#navList li:nth-child(2)");
    active.classList.add("active");

    // Thêm code AJAX
    document.getElementById('searchInput').addEventListener('keyup', function() {
        var search = this.value;
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'app/search-tasks.php', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = function() {
            if(xhr.readyState == 4 && xhr.status == 200) {
                document.getElementById('taskTableBody').innerHTML = xhr.responseText;
            }
        }
        xhr.send('search=' + search);
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