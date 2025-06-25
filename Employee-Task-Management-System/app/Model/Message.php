<?php

// Gửi tin nhắn từ người dùng này đến người dùng khác
function send_message($conn, $sender_id, $receiver_id, $message_text) {
    $sql = "INSERT INTO 
            messages(sender_id, receiver_id, message_text) 
            VALUES(?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$sender_id, $receiver_id, $message_text]);
    
    return $stmt->rowCount() > 0;
}

// Lấy tất cả tin nhắn giữa hai người dùng
function get_messages($conn, $user1_id, $user2_id) {
    $sql = "SELECT * FROM messages 
            WHERE (sender_id = ? AND receiver_id = ?) 
            OR (sender_id = ? AND receiver_id = ?) 
            ORDER BY created_at ASC";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$user1_id, $user2_id, $user2_id, $user1_id]);
    
    if ($stmt->rowCount() > 0) {
        return $stmt->fetchAll();
    } else {
        return [];
    }
}



// // Lấy tất cả tin nhắn chưa đọc của một người dùng
// function get_unread_messages($conn, $user_id) {
//     $sql = "SELECT COUNT(*) as unread_count FROM messages 
//             WHERE receiver_id = ? AND is_read = 0";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$user_id]);
    
//     return $stmt->fetch()['unread_count'];
// }

// // Đánh dấu tin nhắn là đã đọc
// function mark_message_read($conn, $message_id) {
//     $sql = "UPDATE messages SET is_read = 1 WHERE id = ?";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$message_id]);
    
//     return $stmt->rowCount() > 0;
// }

// // Đánh dấu tất cả tin nhắn từ người gửi đến người nhận là đã đọc
// function mark_all_messages_read($conn, $sender_id, $receiver_id) {
//     $sql = "UPDATE messages SET is_read = 1 
//             WHERE sender_id = ? AND receiver_id = ?";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$sender_id, $receiver_id]);
    
//     return $stmt->rowCount() > 0;
// }

// // Lấy tin nhắn cuối cùng giữa hai người dùng
// function get_last_message($conn, $user1_id, $user2_id) {
//     $sql = "SELECT * FROM messages 
//             WHERE (sender_id = ? AND receiver_id = ?) 
//             OR (sender_id = ? AND receiver_id = ?) 
//             ORDER BY created_at DESC LIMIT 1";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$user1_id, $user2_id, $user2_id, $user1_id]);
    
//     if ($stmt->rowCount() > 0) {
//         return $stmt->fetch();
//     } else {
//         return null;
//     }
// }

// // Xóa một tin nhắn
// function delete_message($conn, $message_id, $user_id) {
//     // Chỉ cho phép người gửi xóa tin nhắn của họ
//     $sql = "DELETE FROM messages WHERE id = ? AND sender_id = ?";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$message_id, $user_id]);
    
//     return $stmt->rowCount() > 0;
// }

// // Lấy tất cả người dùng đã trao đổi tin nhắn với người dùng hiện tại
// function get_message_contacts($conn, $user_id) {
//     $sql = "SELECT DISTINCT 
//                 CASE 
//                     WHEN sender_id = ? THEN receiver_id 
//                     ELSE sender_id 
//                 END as contact_id
//             FROM messages 
//             WHERE sender_id = ? OR receiver_id = ?";
//     $stmt = $conn->prepare($sql);
//     $stmt->execute([$user_id, $user_id, $user_id]);
    
//     if ($stmt->rowCount() > 0) {
//         return $stmt->fetchAll();
//     } else {
//         return [];
//     }
// }