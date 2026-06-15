package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class top_k_ma {
    public static void main(String[] args) {
        String ma = "B22DCDT095";
        String qcode = "9XuPzPmy";
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
            
            JsonArray codesArray = dataoj.getAsJsonArray("codes");
            int k = dataoj.get("k").getAsInt();

            Map<String, Integer> frequencyMap = new HashMap<>();
            for (int i = 0; i < codesArray.size(); i++) {
                String code = codesArray.get(i).getAsString();
                frequencyMap.put(code, frequencyMap.getOrDefault(code, 0) + 1);
            }

            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());

            Collections.sort(entryList, (a, b) -> {
                if (!b.getValue().equals(a.getValue())) {
                    return b.getValue().compareTo(a.getValue()); 
                } else {
                    return a.getKey().compareTo(b.getKey()); 
                }
            });

            List<String> resultParts = new ArrayList<>();
            for (int i = 0; i < Math.min(k, entryList.size()); i++) {
                Map.Entry<String, Integer> entry = entryList.get(i);
                resultParts.add(entry.getKey() + "=" + entry.getValue());
            }

            String answer = String.join("|", resultParts);
            System.out.println("Kết quả answer gửi đi: " + answer);

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
            System.out.println("Phản hồi từ server: " + postResponse.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}