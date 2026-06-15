package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class tim_duong_ngan_nhat_bang_dijkstra {
    public static void main(String[] args) {
        // 1. Thông tin cấu hình từ đề bài
        String studentCode = "B22DCDT095"; 
        String qCode = "gZcnaUoB";
        String baseUrl = "http://36.50.135.242:2230/api/rest/object";

        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();

            // ==========================================
            // BƯỚC 1: GỬI REQUEST GET LẤY DỮ LIỆU ĐỒ THỊ
            // ==========================================
            String getUrl = baseUrl + "?studentCode=" + studentCode + "&qCode=" + qCode;
            System.out.println("1. Đang gửi GET request...");

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("-> Dữ liệu JSON nhận được: " + getResponse.body());

            // ==========================================
            // BƯỚC 2: BÓC TÁCH JSON VÀ DỰNG ĐỒ THỊ
            // ==========================================
            JsonObject jsonObject = gson.fromJson(getResponse.body(), JsonObject.class);
            String requestId = jsonObject.get("requestId").getAsString();
            
            JsonObject dataObj = jsonObject.getAsJsonObject("data");
            String startNode = dataObj.get("start").getAsString();
            String endNode = dataObj.get("end").getAsString();
            
            // Lấy danh sách đỉnh (nodes)
            JsonArray nodesArray = dataObj.getAsJsonArray("nodes");
            List<String> nodes = new ArrayList<>();
            for (JsonElement n : nodesArray) {
                nodes.add(n.getAsString());
            }

            // Khởi tạo đồ thị dưới dạng danh sách kề
            // graph.get("A") sẽ trả về một Map chứa các đỉnh kề và trọng số
            Map<String, Map<String, Integer>> graph = new HashMap<>();
            for (String node : nodes) {
                graph.put(node, new HashMap<>());
            }

            // Lấy danh sách cạnh (edges) và đắp vào đồ thị
            JsonArray edgesArray = dataObj.getAsJsonArray("edges");
            for (JsonElement e : edgesArray) {
                JsonObject edge = e.getAsJsonObject();
                
                // Trích xuất linh hoạt các tên Key thường dùng trong đề thi
                String u = edge.get("from").getAsString();
                String v = edge.get("to").getAsString();
                int weight = edge.get("weight").getAsInt();
                // NẾU ĐỀ BÀI LÀ ĐỒ THỊ CÓ HƯỚNG, HÃY XÓA DÒNG PUT (v, u) BÊN DƯỚI!
                graph.get(u).put(v, weight);
                graph.get(v).put(u, weight); 
            }

            // ==========================================
            // BƯỚC 3: CHẠY THUẬT TOÁN DIJKSTRA
            // ==========================================
            Map<String, Integer> distances = new HashMap<>(); // Lưu khoảng cách min
            Map<String, String> previous = new HashMap<>();   // Lưu vết đường đi
            
            for (String node : nodes) {
                distances.put(node, Integer.MAX_VALUE);
            }
            distances.put(startNode, 0);

            // Hàng đợi ưu tiên để luôn duyệt đỉnh có khoảng cách nhỏ nhất
            PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
            pq.add(startNode);

            while (!pq.isEmpty()) {
                String current = pq.poll();

                // Đã tìm tới đích, thoát vòng lặp để tiết kiệm thời gian
                if (current.equals(endNode)) break; 

                // Duyệt qua các hàng xóm của đỉnh hiện tại
                Map<String, Integer> neighbors = graph.get(current);
                for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                    String nextNode = neighbor.getKey();
                    int weight = neighbor.getValue();
                    
                    int newDist = distances.get(current) + weight;
                    if (newDist < distances.get(nextNode)) {
                        distances.put(nextNode, newDist);
                        previous.put(nextNode, current);
                        
                        // Cập nhật lại hàng đợi ưu tiên
                        pq.remove(nextNode);
                        pq.add(nextNode);
                    }
                }
            }

            // ==========================================
            // BƯỚC 4: TRUY VẾT ĐƯỜNG ĐI VÀ FORMAT ĐÁP ÁN
            // ==========================================
            List<String> path = new ArrayList<>();
            String step = endNode;
            
            // Lùi ngược từ đích về điểm xuất phát
            while (step != null) {
                path.add(step);
                step = previous.get(step);
            }
            Collections.reverse(path); // Đảo ngược lại để có chuỗi Start -> End

            String pathString = String.join("->", path);
            int totalCost = distances.get(endNode);
            
            // Format đáp án theo chuẩn: "ChiPhi|N1->N2->N3"
            String answer = totalCost + "|" + pathString;
            System.out.println("2. Kết quả tính toán (answer): " + answer);

            // ==========================================
            // BƯỚC 5: GỬI REQUEST POST NỘP BÀI
            // ==========================================
            System.out.println("3. Đang gửi POST request nộp bài...");
            
            JsonObject submitBody = new JsonObject();
            submitBody.addProperty("studentCode", studentCode);
            submitBody.addProperty("qCode", qCode);
            submitBody.addProperty("requestId", requestId);
            submitBody.addProperty("answer", answer);

            String requestBodyString = gson.toJson(submitBody);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("-> Kết quả từ server: " + postResponse.body());

        } catch (Exception e) {
            System.out.println("CÓ LỖI XẢY RA!");
            e.printStackTrace();
        }
    }
}