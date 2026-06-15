package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class day_tang_dai_nhat_sau_chuyen_hoa_du_lieu {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "nVUwbFjj"; 
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

            int n = valJson.size();
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = valJson.get(i).getAsInt();
            }

            List<Integer> piles = new ArrayList<>();
            for (int x : values) {
                int idx = Collections.binarySearch(piles, x);
                if (idx < 0) {
                    idx = -(idx + 1);
                }
                if (idx < piles.size()) {
                    piles.set(idx, x);
                } else {
                    piles.add(x);
                }
            }

            String answer = String.valueOf(piles.size());
            System.out.println("Kết quả: " + answer);

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
}