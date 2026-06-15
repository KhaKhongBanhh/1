package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class calculate_final_price {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "LckCz7Dt";


        String server =
                "http://36.50.135.242:2230/api/rest/object";



        HttpClient client =
                HttpClient.newHttpClient();




        // =========================
        // a. GET
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

                .uri(
                    URI.create(url)
                )

                .GET()

                .build();





        HttpResponse<String> getResponse =
                client.send(
                    getRequest,
                    HttpResponse.BodyHandlers.ofString()
                );



        System.out.println("GET RESPONSE:");

        System.out.println(
                getResponse.body()
        );





        // =========================
        // b. Parse JSON
        // =========================


        JSONObject response =
                new JSONObject(
                    getResponse.body()
                );



        String requestId =
                response.getString(
                    "requestId"
                );



        JSONObject data =
                response.getJSONObject(
                    "data"
                );




        double price =
                data.getDouble(
                    "price"
                );


        double taxRate =
                data.getDouble(
                    "taxRate"
                );


        double discount =
                data.getDouble(
                    "discount"
                );





        // =========================
        // c. Tính finalPrice
        // =========================


        double finalPrice =
                price
                * (1 + taxRate / 100)
                * (1 - discount / 100);



        finalPrice =
                Math.round(
                    finalPrice * 100
                ) / 100.0;



        System.out.println(
                "finalPrice = "
                + finalPrice
        );






        // =========================
        // d. POST submit
        // =========================


        JSONObject answer =
                new JSONObject();


        answer.put(
                "finalPrice",
                finalPrice
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