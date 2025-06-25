<?php  

function get_all_my_notifications($conn, $id){
    // Thay đổi truy vấn SQL để sắp xếp theo date giảm dần (DESC)
    $sql = "SELECT * FROM notifications WHERE recipient=? ORDER BY date DESC, id DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$id]);

    if($stmt->rowCount() > 0){
        $notifications = $stmt->fetchAll();
    }else $notifications = 0;

    return $notifications;
}


function count_notification($conn, $id){
	$sql = "SELECT id FROM notifications WHERE recipient=? AND is_read=0";
	$stmt = $conn->prepare($sql);
	$stmt->execute([$id]);

	return $stmt->rowCount();
}

function insert_notification($conn, $data){
	$sql = "INSERT INTO notifications (message, recipient, type) VALUES(?,?,?)";
	$stmt = $conn->prepare($sql);
	$stmt->execute($data);
}

function notification_make_read($conn, $recipient_id, $notification_id){
	$sql = "UPDATE notifications SET is_read=1 WHERE id=? AND recipient=?";
	$stmt = $conn->prepare($sql);
	$stmt->execute([$notification_id, $recipient_id]);
}

/**
 * Kiểm tra và tạo thông báo sinh nhật
 * Hàm này sẽ kiểm tra xem hôm nay có ai sinh nhật không và tạo thông báo nếu có
 */
function check_birthdays($conn) {
    // Đầu tiên, xóa tất cả thông báo sinh nhật cũ
    clean_old_birthday_notifications($conn);
    
    // Lấy ngày hiện tại theo định dạng MM-DD
    $today = date('m-d');
    
    // Tìm những người dùng có sinh nhật hôm nay
    $sql = "SELECT * FROM users 
            WHERE birthday IS NOT NULL 
            AND DATE_FORMAT(birthday, '%m-%d') = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute([$today]);
    
    if($stmt->rowCount() > 0){
        $birthday_users = $stmt->fetchAll();
        
        foreach($birthday_users as $user){
            // Kiểm tra xem đã tạo thông báo sinh nhật cho người dùng này hôm nay chưa
            $check_sql = "SELECT id FROM notifications 
                         WHERE type = 'birthday' 
                         AND DATE(date) = CURRENT_DATE()
                         AND message LIKE ?";
                         
            $check_stmt = $conn->prepare($check_sql);
            $check_message = "%{$user['full_name']}%"; // Tìm thông báo có tên người dùng
            $check_stmt->execute([$check_message]);
            
            // Nếu chưa có thông báo, thì tạo mới
            if($check_stmt->rowCount() == 0){
                // Tạo thông báo cho người có sinh nhật
                $message = "🎉 Chúc mừng sinh nhật, {$user['full_name']}! Chúc bạn có một ngày thật vui vẻ và hạnh phúc! 🎂";
                insert_notification($conn, [$message, $user['id'], 'birthday']);
                
                // Tạo thông báo cho tất cả người dùng khác
                $all_users_sql = "SELECT id FROM users WHERE id != ?";
                $all_users_stmt = $conn->prepare($all_users_sql);
                $all_users_stmt->execute([$user['id']]);
                
                if($all_users_stmt->rowCount() > 0){
                    $all_users = $all_users_stmt->fetchAll();
                    foreach($all_users as $other_user){
                        $other_message = "🎂 Hôm nay là sinh nhật của {$user['full_name']}! Hãy cùng gửi lời chúc mừng nhé!";
                        insert_notification($conn, [$other_message, $other_user['id'], 'birthday']);
                    }
                }
            }
        }
        
        return true;
    }
    
    return false;
}

/**
 * Xóa thông báo sinh nhật cũ (không phải của ngày hôm nay)
 * Hàm này sẽ được gọi cùng với check_birthdays để đảm bảo thông báo sinh nhật chỉ tồn tại 1 ngày
 */
function clean_old_birthday_notifications($conn) {
    // Xóa tất cả thông báo sinh nhật có ngày không phải hôm nay
    $sql = "DELETE FROM notifications 
            WHERE type = 'birthday' 
            AND DATE(date) != CURRENT_DATE()";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    
    return $stmt->rowCount(); // Trả về số lượng thông báo đã xóa
}