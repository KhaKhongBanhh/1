<?php 
// bắt đầu session, kiểm tra xem người dùng đã đăng nhập chưa
session_start();
// Hàm isset() kiểm tra xem biến có tồn tại hay không
// Nếu không tồn tại thì sẽ chuyển hướng đến trang đăng nhập
if (isset($_SESSION['role']) && isset($_SESSION['id']) ) {

    include "DB_connection.php";
    include "app/Model/Task.php";
    include "app/Model/User.php";

    if ($_SESSION['role'] == "admin") {
        // Sử dụng các hàm để lấy thông tin từ cơ sở dữ liệu
          $todaydue_task = count_tasks_due_today($conn);
         $overdue_task = count_tasks_overdue($conn);
         $nodeadline_task = count_tasks_NoDeadline($conn);
         $num_task = count_tasks($conn);
         $num_users = count_users($conn);
         $pending = count_pending_tasks($conn);
         $in_progress = count_in_progress_tasks($conn);
         $completed = count_completed_tasks($conn);
    }else {
        $num_my_task = count_my_tasks($conn, $_SESSION['id']);
        $overdue_task = count_my_tasks_overdue($conn, $_SESSION['id']);
        $nodeadline_task = count_my_tasks_NoDeadline($conn, $_SESSION['id']);
        $pending = count_my_pending_tasks($conn, $_SESSION['id']);
         $in_progress = count_my_in_progress_tasks($conn, $_SESSION['id']);
         $completed = count_my_completed_tasks($conn, $_SESSION['id']);

    }
 ?>
<!DOCTYPE html>
<html>
<head>
    <title>Trang chủ</title>
    <!-- Sử dụng được các biểu tượng như fa-users và fa-tasks -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/index.css">
    <!-- Facebook SDK Integration -->
    <div id="fb-root"></div>
    <script async defer crossorigin="anonymous" 
        src="https://connect.facebook.net/vi_VN/sdk.js#xfbml=1&version=v18.0" nonce="randomNonce"></script>
</head>
<body>
    <input type="checkbox" id="checkbox">
    <?php include "inc/header.php" ?>
    <div class="body">
        <?php include "inc/nav.php" ?>
        <section class="section-1">
            <?php if ($_SESSION['role'] == "admin") { ?>
                <div class="dashboard">
                    <div class="dashboard-item">
                        <i class="fa fa-users"></i>
                        <span><?=$num_users?> Nhân viên</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-tasks"></i>
                        <span><?=$num_task?> Tất cả công việc</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-window-close-o"></i>
                        <span><?=$overdue_task?> Quá hạn</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-clock-o"></i>
                        <span><?=$nodeadline_task?> Không có hạn</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-exclamation-triangle"></i>
                        <span><?=$todaydue_task?> Đến hạn hôm nay</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-bell"></i>
                        <span><?=$overdue_task?> Thông báo</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-square-o"></i>
                        <span><?=$pending?> Đang chờ</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-spinner"></i>
                        <span><?=$in_progress?> Đang thực hiện</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-check-square-o"></i>
                        <span><?=$completed?> Đã hoàn thành</span>
                    </div>
                </div>
            <?php }else{ ?>
                <div class="dashboard">
                    <div class="dashboard-item">
                        <i class="fa fa-tasks"></i>
                        <span><?=$num_my_task?> Công việc của tôi</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-window-close-o"></i>
                        <span><?=$overdue_task?> Quá hạn</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-clock-o"></i>
                        <span><?=$nodeadline_task?> Không có hạn</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-square-o"></i>
                        <span><?=$pending?> Đang chờ</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-spinner"></i>
                        <span><?=$in_progress?> Đang thực hiện</span>
                    </div>
                    <div class="dashboard-item">
                        <i class="fa fa-check-square-o"></i>
                        <span><?=$completed?> Đã hoàn thành</span>
                    </div>
                </div>
            <?php } ?>
            
            <!-- Added iframes section - side by side -->
            <div class="iframes-row">
                <div class="iframe-container">
                    <h2><i class="fa fa-facebook-square"></i> Trang Facebook</h2>
                    <!-- Facebook Page Plugin - new format -->
                    <div class="fb-page" 
                        data-href="https://www.facebook.com/HocvienPTIT"
                        data-width="295"
                        data-height="400"
                        data-tabs="timeline"
                        data-small-header="false"
                        data-adapt-container-width="true"
                        data-hide-cover="false"
                        data-show-facepile="true">
                        <blockquote cite="https://www.facebook.com/HocvienPTIT" class="fb-xfbml-parse-ignore">
                            <a href="https://www.facebook.com/HocvienPTIT">Học viện Công nghệ Bưu chính Viễn thông</a>
                        </blockquote>
                    </div>
                </div>
                
                <div class="iframe-container">
                    <h2><i class="fa fa-map-marker"></i> Vị trí văn phòng</h2>
                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.4946681053168!2d106.69908367469967!3d10.771913089387625!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f4670702e31%3A0xa5777fb3a5bb9972!2sBitexco%20Financial%20Tower!5e0!3m2!1sen!2s!4v1715512200000!5m2!1sen!2s" 
                        width="100%" height="400" frameborder="0" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                </div>
            </div>
        </section>
    </div>
    
    <!-- Project Calendar moved to red area in image -->
    <div class="project-calendar-sidebar">
        <h2><i class="fa fa-calendar"></i> Lịch dự án</h2>
        <iframe src="https://calendar.google.com/calendar/embed?src=en.vietnamese%23holiday%40group.v.calendar.google.com&ctz=Asia%2FHo_Chi_Minh" 
            width="100%" height="600" frameborder="0" scrolling="no"></iframe>
    </div>
    
    <!-- Add ChatGPT Chatbot Bubble -->
    <div class="chat-container">
        <div class="chat-box" id="chatBox">
            <div class="chat-header">
                <div class="chat-title">
                    <i class="fa fa-robot"></i> Trợ lý AI
                </div>
                <div class="chat-actions">
                    <button id="minimizeChat"><i class="fa fa-minus"></i></button>
                    <button id="closeChat"><i class="fa fa-times"></i></button>
                </div>
            </div>
            <div class="chat-messages" id="chatMessages">
                <div class="message bot">
                    <div class="message-content">
                        <p>Xin chào! Tôi là trợ lý AI của bạn. Bạn cần giúp đỡ gì hôm nay?</p>
                    </div>
                    <div class="message-time">Vừa xong</div>
                </div>
            </div>
            <div class="chat-input">
                <input type="text" id="userMessage" placeholder="Nhập tin nhắn của bạn...">
                <button id="sendMessage"><i class="fa fa-paper-plane"></i></button>
            </div>
        </div>
        <button class="chat-bubble" id="chatBubble">
            <i class="fa fa-comments"></i>
        </button>
    </div>
    
    <!-- Enhanced modern footer design -->
<footer class="footer">
    <div class="footer-content">
        <div class="footer-section about">
            <h3><i class="fa fa-tasks"></i> Task Pro</h3>
            <p>Quản lý công việc và luồng làm việc của nhóm một cách hiệu quả với giải pháp quản lý công việc toàn diện của chúng tôi.</p>
            <div class="contact">
                <span><i class="fa fa-phone"></i> &nbsp; 0936923716</span>
                <span><i class="fa fa-envelope"></i> &nbsp; traungo456@gmail.com</span>
            </div>
            <div class="socials">
                <a href="#"><i class="fa fa-facebook"></i></a>
                <a href="#"><i class="fa fa-twitter"></i></a>
                <a href="#"><i class="fa fa-instagram"></i></a>
                <a href="#"><i class="fa fa-linkedin"></i></a>
            </div>
        </div>
        <div class="footer-section links">
            <h3>Liên kết nhanh</h3>
            <ul>
                <li><a href="#"><i class="fa fa-angle-right"></i> Trang chủ</a></li>
                <li><a href="#"><i class="fa fa-angle-right"></i> Công việc</a></li>
                <li><a href="#"><i class="fa fa-angle-right"></i> Nhóm</a></li>
                <li><a href="#"><i class="fa fa-angle-right"></i> Báo cáo</a></li>
                <li><a href="#"><i class="fa fa-angle-right"></i> Hỗ trợ</a></li>
            </ul>
        </div>
        <div class="footer-section contact-form">
            <h3>Liên hệ với chúng tôi</h3>
            <form action="index.php" method="post">
                <input type="email" name="email" class="text-input contact-input" placeholder="Địa chỉ email của bạn...">
                <textarea name="message" class="text-input contact-input" placeholder="Tin nhắn của bạn..."></textarea>
                <button type="submit" class="btn btn-primary">Gửi</button>
            </form>
        </div>
    </div>
    <div class="footer-bottom">
        <p>&copy; <?php echo date("Y"); ?> Hệ thống Quản lý Công việc. Tất cả các quyền được bảo lưu.</p>
    </div>
</footer>

<script type="text/javascript">
    // Xử lý sự kiện khi người dùng cuộn trang của FB và Chatbot
    // Đánh dấu menu điều hướng
    var active = document.querySelector("#navList li:nth-child(1)");
    active.classList.add("active");
    
    // Khởi tạo Facebook SDK (là tính năng do Facebook cung cấp) 
    // Hiển thị các chức năng của Facebook như Like, Share, Comment
    window.fbAsyncInit = function() {
        FB.init({
            appId: '',
            autoLogAppEvents: true,
            xfbml: true,
            version: 'v18.0'
        });
        
        // Log when FB SDK is fully loaded
        console.log("Facebook SDK loaded successfully");
        
        // Hiển thị loading indicator khi Facebook Page đang tải
        const fbContainer = document.querySelector('.fb-page');
        if (fbContainer) {
            const loadingIndicator = document.createElement('div');
            loadingIndicator.id = 'fb-loading';
            loadingIndicator.innerHTML = '<p style="text-align:center;padding:20px;"><i class="fa fa-spinner fa-spin"></i> Đang tải Facebook Page...</p>';
            fbContainer.parentNode.insertBefore(loadingIndicator, fbContainer);
            
            // Khi Facebook Page hoàn tất tải, xóa loading indicator
            FB.Event.subscribe('xfbml.render', function() {
                const loader = document.getElementById('fb-loading');
                if (loader) loader.remove();
                console.log("Facebook Page rendered successfully");
            });
        }
    };
    
    // Chat Bubble Functionality
    document.addEventListener('DOMContentLoaded', function() {
        const chatBubble = document.getElementById('chatBubble');
        const chatBox = document.getElementById('chatBox');
        const minimizeChat = document.getElementById('minimizeChat');
        const closeChat = document.getElementById('closeChat');
        const sendMessage = document.getElementById('sendMessage');
        const userMessage = document.getElementById('userMessage');
        const chatMessages = document.getElementById('chatMessages');
        
        // Mở chat khi nhấp vào chat bubble
        chatBubble.addEventListener('click', function() {
            chatBox.style.display = 'flex';
            chatBubble.style.display = 'none';
        });
        
        // Minimize chat
        minimizeChat.addEventListener('click', function() {
            chatBox.style.display = 'none';
            chatBubble.style.display = 'flex';
        });
        
        // Close chat
        closeChat.addEventListener('click', function() {
            chatBox.style.display = 'none';
            chatBubble.style.display = 'flex';
        });
        
        // Send message function
        function sendUserMessage() {
            const message = userMessage.value.trim();
            if (message !== '') {
                // Add user message
                addMessage('user', message);
                userMessage.value = '';
                
                // Simulate bot response after a delay
                setTimeout(function() {
                    getBotResponse(message);
                }, 1000);
            }
        }
        
        // Send message on button click
        sendMessage.addEventListener('click', sendUserMessage);
        
        // Send message on Enter key press
        userMessage.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendUserMessage();
            }
        });
        
        // Add message to chat
        function addMessage(type, text) {
            const messageDiv = document.createElement('div');
            messageDiv.classList.add('message', type);
            
            const time = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            
            messageDiv.innerHTML = `
                <div class="message-content">
                    <p>${text}</p>
                </div>
                <div class="message-time">${time}</div>
            `;
            
            chatMessages.appendChild(messageDiv);
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        // Vietnamese bot responses
        function getBotResponse(message) {
            let response = "Xin lỗi, tôi không hiểu ý bạn. Vui lòng thử lại.";
            
            message = message.toLowerCase();
            
            if (message.includes("xin chào") || message.includes("chào") || message.includes("hello") || message.includes("hi")) {
                response = "Xin chào! Tôi có thể giúp gì cho bạn hôm nay?";
            } else if (message.includes("công việc") || message.includes("task") || message.includes("nhiệm vụ") || message.includes("dự án") || message.includes("project")) {
                response = "Tôi có thể giúp bạn quản lý công việc! Bạn có thể tạo, xem và cập nhật nhiệm vụ trên hệ thống của chúng tôi.";
            } else if (message.includes("giúp") || message.includes("trợ giúp") || message.includes("help")) {
                response = "Tôi sẵn sàng giúp bạn! Bạn có thể hỏi tôi về quản lý nhiệm vụ, cập nhật dự án hoặc cách sử dụng các tính năng cụ thể.";
            } else if (message.includes("thời gian") || message.includes("giờ") || message.includes("ngày") || message.includes("time") || message.includes("date")) {
                response = "Thời gian hiện tại là " + new Date().toLocaleString();
            } else if (message.includes("cảm ơn") || message.includes("thanks") || message.includes("thank")) {
                response = "Không có gì! Tôi có thể giúp gì thêm cho bạn không?";
            } else if (message.includes("deadline") || message.includes("hạn chót") || message.includes("hạn nộp")) {
                response = "Để thiết lập hoặc kiểm tra thời hạn, bạn có thể vào trang chi tiết công việc và cập nhật trường ngày đến hạn.";
            } else if (message.includes("nhân viên") || message.includes("employee") || message.includes("thành viên")) {
                response = "Bạn có thể quản lý nhân viên trong mục 'Quản lý người dùng' trên trang quản trị hệ thống.";
            } else if (message.includes("báo cáo") || message.includes("report") || message.includes("thống kê")) {
                response = "Chức năng báo cáo cho phép bạn theo dõi hiệu suất của dự án và nhân viên. Truy cập mục 'Báo cáo' để biết thêm chi tiết.";
            } else if (message.includes("đăng xuất") || message.includes("logout")) {
                response = "Để đăng xuất, hãy nhấp vào biểu tượng đăng xuất ở cuối menu điều hướng bên trái.";
            } else if (message.includes("đăng nhập") || message.includes("login")) {
                response = "Bạn có thể đăng nhập bằng tên người dùng và mật khẩu được cung cấp bởi quản trị viên hệ thống.";
            }
            
            addMessage('bot', response);
        }
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
