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

public class chon_bao_gia_toi_uu {
    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "ZLBTm2rN"; 
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
            
            double weightKg = dataoj.get("weightKg").getAsDouble();
            int maxEtaDays = dataoj.get("maxEtaDays").getAsInt();
            JsonArray quotesArray = dataoj.getAsJsonArray("quotes");

            List<JsonObject> validQuotes = new ArrayList<>();
            for (int i = 0; i < quotesArray.size(); i++) {
                JsonObject q = quotesArray.get(i).getAsJsonObject();
                int etaDays = q.get("etaDays").getAsInt();
                if (etaDays <= maxEtaDays) {
                    validQuotes.add(q);
                }
            }

            Collections.sort(validQuotes, (a, b) -> {
                double costA = a.get("baseFee").getAsDouble() + (a.get("perKgFee").getAsDouble() * weightKg);
                double costB = b.get("baseFee").getAsDouble() + (b.get("perKgFee").getAsDouble() * weightKg);
                if (Math.abs(costA - costB) > 1e-9) {
                    return Double.compare(costA, costB);
                }
                
                double relA = a.get("reliability").getAsDouble();
                double relB = b.get("reliability").getAsDouble();
                return Double.compare(relB, relA);
            });

            JsonObject bestQuote = validQuotes.get(0);
            String carrier = bestQuote.get("carrier").getAsString();
            int finalEta = bestQuote.get("etaDays").getAsInt();
            
            double finalCost = bestQuote.get("baseFee").getAsDouble() + (bestQuote.get("perKgFee").getAsDouble() * weightKg);
            String costStr = String.format("%.2f", finalCost);

            String answer = carrier + "|" + costStr + "|" + finalEta;
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