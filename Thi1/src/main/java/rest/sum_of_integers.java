package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class sum_of_integers {

    public static void main(String[] args) throws Exception {

        String server = "http://36.50.135.242:2230/api/rest/data";

        String studentCode = "B22DCAT108"; // mã sinh viên
        String qCode = "Gxz5HLjp";       // thay bằng alias được cấp

        HttpClient client = HttpClient.newHttpClient();


        // =========================
        // a,b. GET lấy data
        // =========================

        String getUrl = server 
                + "?studentCode=" + studentCode
                + "&qCode=" + qCode;


        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(getUrl))
                .GET()
                .build();


        HttpResponse<String> getResponse =
                client.send(getRequest,
                        HttpResponse.BodyHandlers.ofString());


        System.out.println("GET Response:");
        System.out.println(getResponse.body());


        // Parse JSON

        JSONObject json = new JSONObject(getResponse.body());


        String requestId = json.getString("requestId");

        JSONArray data = json.getJSONArray("data");


        // =========================
        // c. Tính tổng
        // =========================

        int sum = 0;

        for(int i = 0; i < data.length(); i++){
            sum += data.getInt(i);
        }


        System.out.println("Tong = " + sum);



        // =========================
        // d. POST submit
        // =========================


        JSONObject body = new JSONObject();

        body.put("studentCode", studentCode);
        body.put("qCode", qCode);
        body.put("requestId", requestId);
        body.put("answer", sum);



        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(server + "/submit"))
                .header("Content-Type","application/json")
                .POST(
                    HttpRequest.BodyPublishers
                    .ofString(body.toString())
                )
                .build();



        HttpResponse<String> postResponse =
                client.send(postRequest,
                        HttpResponse.BodyHandlers.ofString());


        System.out.println("POST Response:");
        System.out.println(postResponse.body());

    }
}