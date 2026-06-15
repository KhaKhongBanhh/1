package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class gom_tu_theo_anagram {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "PqRxSqok"; 
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
            JsonArray wordsJson = dataoj.getAsJsonArray("words");

            int n = wordsJson.size();
            String[] words = new String[n];
            for (int i = 0; i < n; i++) {
                words[i] = wordsJson.get(i).getAsString();
            }

            Map<String, List<String>> map = new HashMap<>();
            for (String word : words) {
                char[] chars = word.toCharArray();
                Arrays.sort(chars);
                String sortedWord = new String(chars);
                
                if (!map.containsKey(sortedWord)) {
                    map.put(sortedWord, new ArrayList<>());
                }
                map.get(sortedWord).add(word);
            }

            List<String> groupsList = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                List<String> group = entry.getValue();
                Collections.sort(group);
                String groupStr = String.join(",", group);
                groupsList.add(groupStr);
            }

            Collections.sort(groupsList);

            String answer = String.join("|", groupsList);
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
}