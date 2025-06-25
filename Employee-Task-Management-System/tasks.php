<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "admin") {
    include "DB_connection.php";
    include "app/Model/Task.php";
    include "app/Model/User.php";
    
    // XỬ LÝ LẤY DỮ LIỆU VẼ BIỂU ĐỒ (CHART) - ĐẶT NGAY SAU include, TRƯỚC BẤT KỲ HTML/ECHO NÀO!
    if (isset($_GET['get_chart_data'])) {
        // Lấy số lượng công việc theo từng ngày hạn (due_date) và trạng thái (status)
        $sql = "SELECT due_date, status, COUNT(*) as count FROM tasks GROUP BY due_date, status ORDER BY due_date ASC";
        $stmt = $conn->prepare($sql);
        $stmt->execute([]);
        $raw = $stmt->fetchAll();
        // Tạo mảng labels chứa các ngày hạn
        $labels = [];
        // Tạo mảng map để lưu số lượng công việc theo ngày và trạng thái
        $map = [];
        foreach ($raw as $row) {
            // Lấy ngày hạn (due_date) hoặc 'Không có hạn'
            $date = $row['due_date'] ?: 'Không có hạn';
            // Nếu ngày chưa có trong labels thì thêm vào
            if (!in_array($date, $labels)) $labels[] = $date;
            // Lưu số lượng công việc theo ngày và trạng thái
            $map[$date][$row['status']] = (int)$row['count'];
        }
        // Tạo các mảng về tiến độ hoàn thành công việc cho từng ngày
        $completed = [];
        $in_progress = [];
        $pending = [];
        foreach ($labels as $date) {
            $completed[] = isset($map[$date]['completed']) ? $map[$date]['completed'] : 0;
            $in_progress[] = isset($map[$date]['in_progress']) ? $map[$date]['in_progress'] : 0;
            $pending[] = isset($map[$date]['pending']) ? $map[$date]['pending'] : 0;
        }
        // Trả về dữ liệu JSON cho client để vẽ biểu đồ
        header('Content-Type: application/json');
        echo json_encode([
            'labels' => $labels,
            'completed' => $completed,
            'in_progress' => $in_progress,
            'pending' => $pending
        ]);
        exit();
    }

    $text = "Tất cả công việc";

    // XỬ LÝ TÌM KIẾM BẰNG FORM (GET)
    if (isset($_GET['search']) && !empty($_GET['search'])) {
        $search = "%".$_GET['search']."%";
        $sql = "SELECT * FROM tasks WHERE 
            title LIKE ? OR 
            description LIKE ? OR 
            status LIKE ? OR
            due_date LIKE ?
            ORDER BY 
                CASE 
                    WHEN status = 'pending' THEN 1
                    WHEN status = 'in_progress' THEN 2
                    WHEN status = 'completed' THEN 3
                END,
                title ASC";
        $stmt = $conn->prepare($sql);
        $stmt->execute([$search, $search, $search, $search]);
        $tasks = $stmt->fetchAll();
        $num_task = count($tasks);
        $text = "Kết quả tìm kiếm";
    } else if (isset($_GET['due_date']) &&  $_GET['due_date'] == "Due Today") {
        // Lọc công việc đến hạn hôm nay
        $tasks = get_all_tasks_due_today($conn);
        $num_task = count_tasks_due_today($conn);

    } else if (isset($_GET['due_date']) &&  $_GET['due_date'] == "Overdue") {
        // Lọc công việc quá hạn
        $tasks = get_all_tasks_overdue($conn);
        $num_task = count_tasks_overdue($conn);

    } else if (isset($_GET['due_date']) &&  $_GET['due_date'] == "No Deadline") {
        // Lọc công việc không có hạn
        $tasks = get_all_tasks_NoDeadline($conn);
        $num_task = count_tasks_NoDeadline($conn);

    } else {
        // Lấy tất cả công việc, sắp xếp theo trạng thái và tiêu đề
        $sql = "SELECT * FROM tasks ORDER BY 
            CASE 
                WHEN status = 'pending' THEN 1
                WHEN status = 'in_progress' THEN 2
                WHEN status = 'completed' THEN 3
            END,
            title ASC";
        $stmt = $conn->prepare($sql);
        $stmt->execute([]);
        $tasks = $stmt->fetchAll();
        $num_task = count($tasks);
    }
    $users = get_all_users($conn);
    
    // XỬ LÝ TÌM KIẾM AJAX (KHI GÕ TRONG Ô TÌM KIẾM)
    if (isset($_POST['ajax_search_task'])) {
        $search = trim($_POST['ajax_search_task']);
        $sql = "SELECT * FROM tasks WHERE 
            title LIKE ? OR 
            description LIKE ? OR 
            status LIKE ? OR
            due_date LIKE ?
            ORDER BY 
                CASE 
                    WHEN status = 'pending' THEN 1
                    WHEN status = 'in_progress' THEN 2
                    WHEN status = 'completed' THEN 3
                END,
                title ASC";
        $stmt = $conn->prepare($sql);
        $search_param = "%$search%";
        $stmt->execute([$search_param, $search_param, $search_param, $search_param]);
        $tasks = $stmt->fetchAll();
        $users = get_all_users($conn);
        if($tasks) {
            $i = 0;
            foreach($tasks as $task) {
                $i++;
                
                // Việt hóa trạng thái công việc cho phần AJAX
                $statusText = $task['status'];
                if ($statusText == 'pending') {
                    $statusText = 'Đang chờ';
                } else if ($statusText == 'in_progress') {
                    $statusText = 'Đang thực hiện';
                } else if ($statusText == 'completed') {
                    $statusText = 'Đã hoàn thành';
                }
                
                // Việt hóa deadline
                $dueDateText = ($task['due_date'] == '') ? 'Không có hạn' : $task['due_date'];
                
                // Hiển thị từng dòng công việc (dạng <tr>) cho bảng
                echo "<tr>
                    <td>".$i."</td>
                    <td>".$task['title']."</td>
                    <td>".$task['description']."</td>
                    <td>";
                foreach ($users as $user) {
                    if($user['id'] == $task['assigned_to']){
                        echo $user['full_name'];
                    }
                }
                echo "</td>
                    <td>".$dueDateText."</td>
                    <td>".$statusText."</td>
                    <td>
                        <a href='edit-task.php?id=".$task['id']."' class='edit-btn'>Sửa</a>
                        <a href='delete-task.php?id=".$task['id']."' class='delete-btn'>Xóa</a>
                    </td>
                </tr>";
            }
        } else {
            // Nếu không có công việc nào phù hợp
            echo "<tr><td colspan='7' style='text-align: center;'>Không tìm thấy công việc nào</td></tr>";
        }
        exit();
    }

 ?>
<!DOCTYPE html>
<html>
<head>
    <title>Tất cả công việc</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/tasks.css">
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <h4 class="title-2">
                <a href="create_task.php" class="btn">Tạo công việc</a>
                <a href="tasks.php?due_date=Due Today" class="filter-btn due<?php if(isset($_GET['due_date']) && $_GET['due_date'] == 'Due Today') echo ' active'; ?>">Đến hạn hôm nay</a>
                <a href="tasks.php?due_date=Overdue" class="filter-btn overdue<?php if(isset($_GET['due_date']) && $_GET['due_date'] == 'Overdue') echo ' active'; ?>">Quá hạn</a>
                <a href="tasks.php?due_date=No Deadline" class="filter-btn nodeadline<?php if(isset($_GET['due_date']) && $_GET['due_date'] == 'No Deadline') echo ' active'; ?>">Không có hạn</a>
                <a href="tasks.php" class="filter-btn all<?php if(!isset($_GET['due_date']) && !isset($_GET['search'])) echo ' active'; ?>">Tất cả công việc</a>
                <button id="showChartBtn" class="filter-btn chart-btn" style="background:#fff;color:#3498db;border:1px solid #3498db;margin-left:10px;">Vẽ biểu đồ</button>
            </h4>
         <h4 class="title-2"><?=$text?> (<?=$num_task?>)</h4>
            <?php if (isset($_GET['success'])) {?>
                <div class="success" role="alert">
              <?php echo stripcslashes($_GET['success']); ?>
            </div>
        <?php } ?>
            <form action="" method="GET" class="search-form">
                <input type="text" name="search" id="searchInput" placeholder="Tìm kiếm theo tiêu đề, mô tả hoặc trạng thái" value="<?php if(isset($_GET['search'])) echo htmlspecialchars($_GET['search']); ?>">
                <button type="submit" class="search-btn">Tìm kiếm</button>
                <a href="tasks.php" class="clear-btn">Xóa</a>
            </form>
            <?php if ($tasks != 0) { ?>
            <table class="main-table">
                <tr>
                    <th>#</th>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Giao cho</th>
                    <th>Hạn hoàn thành</th>
                    <th>Trạng thái</th>
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
                    <td>
                        <?php 
                  foreach ($users as $user) {
                        if($user['id'] == $task['assigned_to']){
                            echo $user['full_name'];
                        }}?>
                </td>
                <td><?php if($task['due_date'] == "") echo "Không có hạn";
                          else echo $task['due_date'];
                   ?></td>
                <td><?=$statusText?></td>
                    <td>
                        <a href="edit-task.php?id=<?=$task['id']?>" class="edit-btn">Sửa</a>
                        <a href="delete-task.php?id=<?=$task['id']?>" class="delete-btn">Xóa</a>
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

<!-- Modal hiển thị biểu đồ -->
<div id="chartModal" style="display:none;position:fixed;top:0;left:0;width:100vw;height:100vh;background:rgba(0,0,0,0.3);z-index:999;align-items:center;justify-content:center;">
  <div style="background:#fff;padding:30px 20px 20px 20px;border-radius:10px;min-width:350px;min-height:350px;position:relative;max-width:90vw;max-height:90vh;">
    <button onclick="document.getElementById('chartModal').style.display='none'" style="position:absolute;top:10px;right:10px;font-size:20px;background:none;border:none;cursor:pointer;">&times;</button>
    <canvas id="tasksChart" width="600" height="350"></canvas>
  </div>
</div>

<!-- Thêm Chart.js CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
// XỬ LÝ HIỂN THỊ BIỂU ĐỒ KHI BẤM NÚT "Vẽ biểu đồ"
document.getElementById('showChartBtn').onclick = function() {
  document.getElementById('chartModal').style.display = 'flex';
  if(window.tasksChartDrawn) return;
  fetch('tasks.php?get_chart_data=1')
    .then(res => res.json())
    .then(data => {
      const ctx = document.getElementById('tasksChart').getContext('2d');
      window.tasksChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.labels,
          datasets: [
            {
              label: 'Đã hoàn thành',
              data: data.completed,
              backgroundColor: '#4CAF50',
            },
            {
              label: 'Đang thực hiện',
              data: data.in_progress,
              backgroundColor: '#FFD600',
            },
            {
              label: 'Đang chờ',
              data: data.pending,
              backgroundColor: '#F44336',
            }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: 'top' },
            title: { display: true, text: 'Thống kê số lượng công việc theo ngày' }
          },
          scales: {
            x: { title: { display: true, text: 'Ngày' } },
            y: { title: { display: true, text: 'Số lượng công việc' }, beginAtZero: true }
          }
        }
      });
      window.tasksChartDrawn = true;
    });
};
</script>

<script type="text/javascript">
// ĐÁNH DẤU MENU NAVIGATION
var active = document.querySelector("#navList li:nth-child(4)");
active.classList.add("active");

// XỬ LÝ TÌM KIẾM AJAX KHI GÕ TRONG Ô TÌM KIẾM
document.getElementById('searchInput').addEventListener('keyup', function() {
    // Lấy giá trị tìm kiếm
    var search = this.value;
    // Gửi yêu cầu AJAX đến tasks.php
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'tasks.php', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = function() {
        if(xhr.readyState == 4 && xhr.status == 200) {
            // Cập nhật lại bảng công việc với kết quả trả về
            document.getElementById('taskTableBody').innerHTML = xhr.responseText;
        }
    }
    // Gửi dữ liệu tìm kiếm lên server
    xhr.send('ajax_search_task=' + encodeURIComponent(search));
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