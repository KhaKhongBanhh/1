package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;



public class doi_soat_thanh_toan {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "2kKNY9h9";


        String server =
                "http://36.50.135.242:2230/api/rest/data";


        HttpClient client =
                HttpClient.newHttpClient();



        // =========================
        // a. GET DATA
        // =========================


        String url =
                server
                + "?studentCode="
                + URLEncoder.encode(
                    studentCode,
                    StandardCharsets.UTF_8
                )
                + "&qCode="
                + URLEncoder.encode(
                    qCode,
                    StandardCharsets.UTF_8
                );


        System.out.println("GET URL:");
        System.out.println(url);



        HttpRequest getRequest =
                HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();



        HttpResponse<String> getResponse =
                client.send(
                    getRequest,
                    HttpResponse.BodyHandlers.ofString()
                );



        System.out.println("GET RESPONSE:");

        System.out.println(getResponse.body());



        // =========================
        // b. Parse JSON
        // =========================


        JSONObject response =
                new JSONObject(
                    getResponse.body()
                );


        String requestId =
                response.getString("requestId");



        JSONArray data =
                response.getJSONArray("data");




        // =========================
        // c. Tính toán
        // =========================


        double capturedTotal = 0;

        double refundedTotal = 0;

        int failedCount = 0;



        for(int i = 0; i < data.length(); i++){


            JSONObject transaction =
                    data.getJSONObject(i);



            double amount =
                    transaction.getDouble("amount");



            String status =
                    transaction.getString("status");



            if(status.equals("CAPTURED")){

                capturedTotal += amount;

            }


            else if(status.equals("REFUNDED")){

                refundedTotal += amount;

            }


            else if(status.equals("FAILED")){

                failedCount++;

            }


        }



        double netTotal =
                capturedTotal - refundedTotal;



        // làm tròn 2 số

        capturedTotal =
                Math.round(capturedTotal * 100.0) / 100.0;


        refundedTotal =
                Math.round(refundedTotal * 100.0) / 100.0;


        netTotal =
                Math.round(netTotal * 100.0) / 100.0;




        System.out.println(
            "capturedTotal = " + capturedTotal
        );

        System.out.println(
            "refundedTotal = " + refundedTotal
        );

        System.out.println(
            "netTotal = " + netTotal
        );

        System.out.println(
            "failedCount = " + failedCount
        );





        // =========================
        // d. POST submit
        // =========================


        JSONObject answer =
                new JSONObject();


        answer.put(
            "capturedTotal",
            capturedTotal
        );


        answer.put(
            "refundedTotal",
            refundedTotal
        );


        answer.put(
            "netTotal",
            netTotal
        );


        answer.put(
            "failedCount",
            failedCount
        );



        JSONObject body =
                new JSONObject();



        body.put(
            "studentCode",
            studentCode
        );


        body.put(
            "qCode",
            qCode
        );


        body.put(
            "requestId",
            requestId
        );


        body.put(
            "answer",
            answer
        );





        HttpRequest postRequest =
                HttpRequest.newBuilder()

                .uri(
                    URI.create(
                        server + "/submit"
                    )
                )

                .header(
                    "Content-Type",
                    "application/json"
                )

                .POST(
                    HttpRequest.BodyPublishers
                    .ofString(
                        body.toString()
                    )
                )

                .build();





        HttpResponse<String> postResponse =
                client.send(
                    postRequest,
                    HttpResponse.BodyHandlers.ofString()
                );




        System.out.println(
            "POST RESPONSE:"
        );


        System.out.println(
            postResponse.body()
        );


    }
}