package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class khop_nhieu_mau_trong_log {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "IzQTXgQH"; 
        String url = "http://36.50.135.242:2230/api/rest/character"; 
        
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
            
            String text = dataoj.get("text").getAsString();
            JsonArray patternsJson = dataoj.getAsJsonArray("patterns");

            List<String> resultParts = new ArrayList<>();

            for (int i = 0; i < patternsJson.size(); i++) {
                String pattern = patternsJson.get(i).getAsString();
                int count = countMatchesWithOverlap(text, pattern);
                resultParts.add(pattern + "=" + count);
            }

            String answer = String.join("|", resultParts);
            System.out.println("Kết quả answer: " + answer);

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
            System.out.println("Phản hồi: " + postResponse.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countMatchesWithOverlap(String text, String pattern) {
        if (pattern == null || pattern.isEmpty() || text == null || text.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index++; 
        }
        
        return count;
    }
}