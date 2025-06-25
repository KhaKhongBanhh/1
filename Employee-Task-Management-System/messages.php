<?php 
session_start();
// Kiểm tra đăng nhập
if (isset($_SESSION['role']) && isset($_SESSION['id']) ) {
    include "DB_connection.php";
    include "app/Model/User.php";
    include "app/Model/Message.php";
    
    // Lấy tất cả user để hiển thị danh bạ
    $users = get_all_users($conn);
    $current_user_id = $_SESSION['id'];
    
    // Lấy id người dùng đang chọn để chat (nếu có)
    $selected_user_id = isset($_GET['user']) ? $_GET['user'] : null;
    $selected_user = null;
    
    if ($selected_user_id) {
        // Lấy thông tin user đang chat
        $selected_user = get_user_by_id($conn, $selected_user_id);
    }
    
    // Xử lý gửi tin nhắn
    if (isset($_POST['message']) && !empty($_POST['message']) && $selected_user_id) {
        $message_text = $_POST['message'];
        send_message($conn, $current_user_id, $selected_user_id, $message_text);
        
        // Redirect để tránh gửi lại form khi reload
        header("Location: messages.php?user=".$selected_user_id);
        exit();
    }
    
    // Lấy danh sách tin nhắn giữa 2 người (nếu đã chọn user)
    $messages = [];
    if ($selected_user_id) {
        $messages = get_messages($conn, $current_user_id, $selected_user_id);
    }
 ?>
<!DOCTYPE html>
<html>
<head>
    <title>Tin nhắn</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/messages.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        
        <div class="messages-container">
            <!-- Sidebar danh bạ liên hệ -->
            <div class="contacts-sidebar">
                <div class="contacts-header">
                    <h3><i class="fa fa-comment"></i> Tin nhắn</h3>
                </div>
                <div class="contacts-search">
                    <input type="text" id="search-contacts" placeholder="Tìm kiếm liên hệ...">
                </div>
                <ul class="contacts-list">
                    <?php foreach($users as $user): ?>
                        <?php if($user['id'] != $_SESSION['id']): ?>
                            <li class="contact-item <?php echo ($selected_user_id == $user['id']) ? 'active' : ''; ?>" 
                                onclick="window.location.href='messages.php?user=<?=$user['id']?>'">
                                <div class="contact-avatar">
                                    <img src="img/user.png" alt="<?=$user['full_name']?>">
                                </div>
                                <div class="contact-info">
                                    <div class="contact-name"><?=$user['full_name']?></div>
                                    <div class="contact-status">
                                        <?php 
                                            $role = $user['role'] == 'admin' ? 'Quản trị viên' : 'Nhân viên'; 
                                            echo $role;
                                        ?>
                                    </div>
                                </div>
                            </li>
                        <?php endif; ?>
                    <?php endforeach; ?>
                </ul>
            </div>
            
            <!-- Khu vực chat chính -->
            <div class="chat-area">
                <?php if($selected_user): ?>
                    <!-- Header chat với thông tin người đang chat -->
                    <div class="chat-header">
                        <div style="display: flex; align-items: center;">
                            <div class="contact-avatar">
                                <img src="img/user.png" alt="<?=$selected_user['full_name']?>">
                            </div>
                            <div class="contact-info">
                                <div class="contact-name"><?=$selected_user['full_name']?></div>
                                <div class="contact-status">
                                    <?php 
                                        $role = $selected_user['role'] == 'admin' ? 'Quản trị viên' : 'Nhân viên'; 
                                        echo $role;
                                    ?>
                                </div>
                            </div>
                        </div>
                        <div class="header-actions">
                            <!-- Các nút gọi điện, video, info -->
                            <i class="fa fa-phone"></i>
                            <i class="fa fa-video-camera"></i>
                            <i class="fa fa-info-circle"></i>
                        </div>
                    </div>
                    
                    <!-- Hiển thị tin nhắn -->
                    <div class="chat-messages" id="chat-messages">
                        <?php if(empty($messages)): ?>
                            <div style="text-align: center; padding: 20px; color: #9aa3b5;">
                                Bắt đầu cuộc trò chuyện với <?=$selected_user['full_name']?>
                            </div>
                        <?php else: ?>
                            <?php foreach($messages as $msg): ?>
                                <div class="message <?= ($msg['sender_id'] == $_SESSION['id']) ? 'sent' : 'received' ?>">
                                    <div class="message-content">
                                        <?= htmlspecialchars($msg['message_text']) ?>
                                    </div>
                                    <div class="message-time">
                                        <?= date('d/m/Y H:i', strtotime($msg['created_at'])) ?>
                                    </div>
                                </div>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </div>
                    
                    <!-- Ô nhập tin nhắn -->
                    <div class="chat-input">
                        <form method="post" action="messages.php?user=<?=$selected_user_id?>">
                            <div class="input-actions">
                                <i class="fa fa-plus-circle"></i>
                                <i class="fa fa-picture-o"></i>
                                <i class="fa fa-smile-o"></i>
                            </div>
                            <input type="text" name="message" placeholder="Nhập tin nhắn..." required>
                            <button type="submit"><i class="fa fa-paper-plane"></i></button>
                        </form>
                    </div>
                <?php else: ?>
                    <!-- Nếu chưa chọn người chat -->
                    <div class="no-user-selected">
                        <i class="fa fa-comments-o"></i>
                        <h3>Hệ thống tin nhắn công ty</h3>
                        <p>Chọn một người dùng từ danh sách bên trái để bắt đầu cuộc trò chuyện</p>
                    </div>
                <?php endif; ?>
            </div>
        </div>
    </div>
    
    <!-- Thêm audio để phát tiếng đối phương khi gọi điện -->
    <audio id="remoteAudio" autoplay></audio>
    
    <!-- Thư viện PeerJS để gọi điện/audio -->
    <script src="https://unpkg.com/peerjs@1.5.2/dist/peerjs.min.js"></script>
    <script>
    // Tạo peerId cho user hiện tại
    const myPeerId = "user_" + <?=json_encode($_SESSION['id'])?>;
    const peer = new Peer(myPeerId, {
      host: 'peerjs.com',
      port: 443,
      secure: true
    });

    // Khi nhận cuộc gọi đến
    peer.on('call', function(call) {
      navigator.mediaDevices.getUserMedia({ audio: true }).then(function(stream) {
        call.answer(stream); // gửi stream của mình cho đối phương
        call.on('stream', function(remoteStream) {
          document.getElementById('remoteAudio').srcObject = remoteStream;
        });
        // Có thể hiện popup "Đang trò chuyện với ..." ở đây
      });
    });

    // Khi bấm nút gọi điện
    var callBtn = document.querySelector('.fa-phone');
    if (callBtn) {
      callBtn.addEventListener('click', function(e) {
        e.preventDefault();
        const otherPeerId = "user_" + <?=json_encode($selected_user_id ?? 0)?>;
        if (otherPeerId === myPeerId || otherPeerId === "user_0") {
          alert("Không thể tự gọi cho chính mình hoặc chưa chọn người nhận!");
          return;
        }
        navigator.mediaDevices.getUserMedia({ audio: true }).then(function(stream) {
          const call = peer.call(otherPeerId, stream);
          if (call && typeof call.on === 'function') {
            call.on('stream', function(remoteStream) {
              document.getElementById('remoteAudio').srcObject = remoteStream;
            });
            // Có thể hiện popup "Đang gọi cho ..." ở đây
          } else {
            alert("Không thể kết nối tới người nhận. Có thể họ chưa mở trang chat hoặc đang offline.");
            return;
          }
        }).catch(function(err) {
          alert("Không truy cập được micro: " + err);
        });
      });
    }
    </script>
    
    <script>
        // Fix lỗi scroll khi load trang
        window.addEventListener('load', function() {
            // Ẩn thanh cuộn của body
            document.body.style.overflow = 'hidden';
            
            // Tự động cuộn xuống cuối tin nhắn
            var chatMessages = document.getElementById('chat-messages');
            if (chatMessages) {
                chatMessages.scrollTop = chatMessages.scrollHeight;
            }
        });
        
        // Đánh dấu menu Tin nhắn là active
        var active = document.querySelector("#navList li:nth-child(5)");
        active.classList.add("active");
        
        // Tự động cuộn xuống cuối khi có tin nhắn mới
        var chatMessages = document.getElementById('chat-messages');
        if (chatMessages) {
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        // Lọc danh bạ khi tìm kiếm
        document.getElementById('search-contacts').addEventListener('keyup', function() {
            var filter = this.value.toLowerCase();
            var contactItems = document.querySelectorAll('.contact-item');
            
            contactItems.forEach(function(item) {
                var name = item.querySelector('.contact-name').textContent.toLowerCase();
                if (name.includes(filter)) {
                    item.style.display = '';
                } else {
                    item.style.display = 'none';
                }
            });
        });
        
        // Đảm bảo luôn cuộn xuống cuối khi load xong DOM
        document.addEventListener('DOMContentLoaded', function() {
            var chatMessages = document.getElementById('chat-messages');
            if (chatMessages) {
                chatMessages.scrollTop = chatMessages.scrollHeight;
            }
        });
    </script>
</body>
</html>
<?php }else{ 
   // Nếu chưa đăng nhập thì chuyển về trang đăng nhập
   $em = "Vui lòng đăng nhập trước";
   header("Location: login.php?error=$em");
   exit();
}
?>
