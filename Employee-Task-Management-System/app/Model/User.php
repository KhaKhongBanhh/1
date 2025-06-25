<?php 

function get_all_users($conn){
    $sql = "SELECT * FROM users ORDER BY 
            CASE 
                WHEN role = 'admin' THEN 1 
                ELSE 2 
            END,
            full_name ASC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();

    if($stmt->rowCount() > 0){
        $users = $stmt->fetchAll();
    }else $users = 0;

    return $users;
}


function insert_user($conn, $data){
    $sql = "INSERT INTO users (full_name, username, password, role, birthday) VALUES(?,?,?,?,?)";
    $stmt = $conn->prepare($sql);
    $stmt->execute($data);
}

function update_user($conn, $data){
	$sql = "UPDATE users SET full_name=?, username=?, password=?, role=?, birthday=? WHERE id=?";
	$stmt = $conn->prepare($sql);
	$stmt->execute([$data[0], $data[1], $data[2], $data[3], $data[4], $data[5]]);
}

function delete_user($conn, $data){
	// Xóa các task liên quan trước
	$sql1 = "DELETE FROM tasks WHERE assigned_to=?";
	$stmt1 = $conn->prepare($sql1);
	$stmt1->execute($data);

	// Sau đó xóa user
	$sql2 = "DELETE FROM users WHERE id=?";
	$stmt2 = $conn->prepare($sql2);
	$stmt2->execute($data);
}


function get_user_by_id($conn, $id){
	$sql = "SELECT * FROM users WHERE id =? ";
	$stmt = $conn->prepare($sql);
	$stmt->execute([$id]);

	if($stmt->rowCount() > 0){
		$user = $stmt->fetch();
	}else $user = 0;

	return $user;
}

function update_profile($conn, $data){
	$sql = "UPDATE users SET full_name=?,  password=? WHERE id=? ";
	$stmt = $conn->prepare($sql);
	$stmt->execute($data);
}

function count_users($conn){
	$sql = "SELECT id FROM users WHERE role='employee'";
	$stmt = $conn->prepare($sql);
	$stmt->execute([]);

	return $stmt->rowCount();
}

function check_username($conn, $username){
	$sql = "SELECT username FROM users WHERE username=?";
	$stmt = $conn->prepare($sql);
	$stmt->execute([$username]);

	if($stmt->rowCount() > 0){
		return true;
	}
	return false;
}

function search_users($conn, $search_term){
    // Tạo chuỗi tìm kiếm với ký tự % để tìm kiếm theo kiểu LIKE
    $search_term = "%{$search_term}%";
    
    // Tạo câu lệnh SQL
    $sql = "SELECT * FROM users 
            WHERE full_name LIKE ? 
            OR username LIKE ?
            OR role LIKE ?
            ORDER BY 
            CASE 
                WHEN role = 'admin' THEN 1 
                ELSE 2 
            END,
            full_name ASC";
    // Sử dụng prepared statement để tránh SQL injection
    $stmt = $conn->prepare($sql);
    // Truyền tham số vào câu lệnh
    $stmt->execute([$search_term, $search_term, $search_term]);

    // Kiểm tra xem có kết quả nào không
     // Nếu có, trả về danh sách người dùng
     // Nếu không, trả về 0
    if ($stmt->rowCount() >= 1) {
        $users = $stmt->fetchAll();
        return $users;
    } else {
        return 0;
    }
}