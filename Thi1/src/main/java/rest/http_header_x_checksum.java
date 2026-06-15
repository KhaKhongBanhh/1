package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class http_header_x_checksum {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "xYJAWD7t";


        String server =
                "http://36.50.135.242:2230/api/rest/header";



        HttpClient client =
                HttpClient.newHttpClient();




        // =========================
        // a. GET phase 1
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
        // c. đọc header X-Checksum
        // =========================


        String checksum =
                getResponse.headers()
                .firstValue("X-Checksum")
                .orElse("");



        System.out.println(
                "X-Checksum = "
                + checksum
        );






        // =========================
        // b. lấy requestId
        // =========================


        JSONObject json =
                new JSONObject(
                    getResponse.body()
                );



        String requestId =
                json.getString(
                    "requestId"
                );





        // =========================
        // d. POST phase 2
        // =========================



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


                // gửi lại checksum nhận được

                .header(
                    "X-Checksum",
                    checksum
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