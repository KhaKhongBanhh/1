<?php  

$sName = "localhost";
$uName = "root";
$pass  = "";
$db_name = "task_management_db";

try {
	$conn = new PDO("mysql:host=$sName;dbname=$db_name", $uName, $pass);
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
}catch(PDOExeption $e){
	echo "Connection failed: ". $e->getMessage();
	exit;
}


// <?php  

// $host = "sql306.infinityfree.com";
// $dbname = "if0_38954884_task_management_db";
// $username = "if0_38954884";
// $password = "traungo456";

// try {
//     // Sử dụng đúng tên biến
//     $conn = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
//     $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
// } catch (PDOException $e) { // Chữ đúng là PDOException, không phải PDOExeption
//     echo "Connection failed: " . $e->getMessage();
//     exit;
// }
// ?>