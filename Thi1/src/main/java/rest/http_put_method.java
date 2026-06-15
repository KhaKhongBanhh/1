package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class http_put_method {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "ytSOOABc";


        String server =
                "http://36.50.135.242:2230/api/rest/method";



        HttpClient client =
                HttpClient.newHttpClient();




        // =========================
        // a. GET task
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
        // b. Lấy requestId
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



        System.out.println(
                "ID: "
                + data.get("id")
        );


        System.out.println(
                "TITLE: "
                + data.get("title")
        );


        System.out.println(
                "STATUS: "
                + data.get("status")
        );





        // =========================
        // c,d. PUT submit
        // =========================



        JSONObject answer =
                new JSONObject();


        answer.put(
                "status",
                "done"
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
                "answer",
                answer
        );





        String putUrl =
                server
                + "/"
                + requestId;





        HttpRequest putRequest =
                HttpRequest.newBuilder()

                .uri(
                    URI.create(
                        putUrl
                    )
                )

                .header(
                    "Content-Type",
                    "application/json"
                )


                .PUT(
                    HttpRequest.BodyPublishers
                    .ofString(
                        body.toString()
                    )
                )


                .build();






        HttpResponse<String> putResponse =
                client.send(
                    putRequest,
                    HttpResponse.BodyHandlers.ofString()
                );





        System.out.println(
                "PUT RESPONSE:"
        );


        System.out.println(
                putResponse.body()
        );



    }
}