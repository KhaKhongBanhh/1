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

public class lap_lich_theo_deadline {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "LqB76htv"; 
        String url = "http://36.50.135.242:2230/api/rest/object"; 
        
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
            JsonArray jobsArray = dataoj.getAsJsonArray("jobs");

            List<JsonObject> jobsList = new ArrayList<>();
            for (int i = 0; i < jobsArray.size(); i++) {
                jobsList.add(jobsArray.get(i).getAsJsonObject());
            }

            Collections.sort(jobsList, (a, b) -> {
                int endA = a.get("end").getAsInt();
                int endB = b.get("end").getAsInt();
                return Integer.compare(endA, endB);
            });

            List<String> selectedJobs = new ArrayList<>();
            int lastEndTime = -1;

            String idKey = jobsList.get(0).has("id") ? "id" : "name";

            for (JsonObject job : jobsList) {
                int start = job.get("start").getAsInt();
                if (start >= lastEndTime) {
                    selectedJobs.add(job.get(idKey).getAsString());
                    lastEndTime = job.get("end").getAsInt();
                }
            }

            String answer = String.join(",", selectedJobs);
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