package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayDeque;
import java.util.Deque;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class tong_lon_nhat_cua_cua_so_con {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "pdRFrkGq";
        String url = "http://36.50.135.242:2230/api/rest/data"; 
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();
            String geturl = url + "?studentCode=" + ma + "&qCode=" + qcode;

            HttpRequest get = HttpRequest.newBuilder().uri(URI.create(geturl)).GET().build();
            HttpResponse<String> getres = client.send(get, HttpResponse.BodyHandlers.ofString());
            String getresBody = getres.body();
            
            JsonObject oj = gson.fromJson(getresBody, JsonObject.class);
            String rqid = oj.get("requestId").getAsString();
            JsonObject dataoj = oj.getAsJsonObject("data");
            
            JsonArray valJson = dataoj.getAsJsonArray("values");
            int k = dataoj.get("k").getAsInt();

            int n = valJson.size();
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = valJson.get(i).getAsInt();
            }

            long[] P = new long[n + 1];
            P[0] = 0;
            for (int i = 1; i <= n; i++) {
                P[i] = P[i - 1] + values[i - 1];
            }

            long maxSum = Long.MIN_VALUE;
            Deque<Integer> deque = new ArrayDeque<>();
 
            deque.addLast(0);

            for (int j = 1; j <= n; j++) {
       
                while (!deque.isEmpty() && deque.peekFirst() < j - k) {
                    deque.pollFirst();
                }

       
                if (!deque.isEmpty()) {
                    long currentSum = P[j] - P[deque.peekFirst()];
                    if (currentSum > maxSum) {
                        maxSum = currentSum;
                    }
                }

       
                while (!deque.isEmpty() && P[deque.peekLast()] >= P[j]) {
                    deque.pollLast();
                }
                deque.addLast(j);
            }

            String answer = String.valueOf(maxSum);
            System.out.println("Kết quả tính toán answer: " + answer);

            JsonObject submitBody = new JsonObject();
            submitBody.addProperty("studentCode", ma);
            submitBody.addProperty("qCode", qcode);
            submitBody.addProperty("requestId", rqid);
            submitBody.addProperty("answer", answer);

            String requestBodyString = gson.toJson(submitBody);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Phản hồi từ Server: " + postResponse.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}