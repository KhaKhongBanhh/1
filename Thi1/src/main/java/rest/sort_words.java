package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;



public class sort_words {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "Awoh93d1";


        String server =
                "http://36.50.135.242:2230/api/rest/character";



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
                .uri(URI.create(url))
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
        // b. Lấy requestId và data
        // =========================


        JSONObject json =
                new JSONObject(
                        getResponse.body()
                );



        String requestId =
                json.getString("requestId");



        String data =
                json.getString("data");




        // =========================
        // c. Tách và sort
        // =========================


        String[] words =
                data.split(" ");



        Arrays.sort(words);



        String answer =
                String.join(
                        " ",
                        words
                );



        System.out.println("ANSWER:");

        System.out.println(answer);





        // =========================
        // d. POST submit
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