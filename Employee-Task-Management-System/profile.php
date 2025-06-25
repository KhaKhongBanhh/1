<?php 
session_start();
if (isset($_SESSION['role']) && isset($_SESSION['id']) && $_SESSION['role'] == "employee") {
    include "DB_connection.php";
    include "app/Model/User.php";
    $user = get_user_by_id($conn, $_SESSION['id']);
    
 ?>
<!DOCTYPE html>
<html>
<head>
	<title>Hồ sơ</title>
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="css/style.css">
	<link rel="stylesheet" href="css/profile.css">
</head>
<body>
	<input type="checkbox" id="checkbox">
	<?php include "inc/header.php" ?>
	<div class="body">
		<?php include "inc/nav.php" ?>
		<section class="section-1">
			<h4 class="title">Hồ sơ <a href="edit_profile.php">Chỉnh sửa hồ sơ</a></h4>
         <table class="main-table" style="max-width: 300px;">
				<tr>
					<td>Họ tên</td>
					<td><?=$user['full_name']?></td>
				</tr>
				<tr>
					<td>Tên đăng nhập</td>
					<td><?=$user['username']?></td>
				</tr>
				<tr>
					<td>Ngày sinh</td>
					<td><?php echo isset($user['birthday']) && !empty($user['birthday']) ? htmlspecialchars($user['birthday']) : 'Chưa thiết lập'; ?></td>
				</tr>
				<tr>
					<td>Ngày tham gia</td>
					<td><?=$user['created_at']?></td>
				</tr>
			</table>

		</section>
	</div>

<script type="text/javascript">
	var active = document.querySelector("#navList li:nth-child(3)");
	active.classList.add("active");
</script>
</body>
</html>
<?php }else{ 
   $em = "Vui lòng đăng nhập trước";
   header("Location: login.php?error=$em");
   exit();
}
 ?>