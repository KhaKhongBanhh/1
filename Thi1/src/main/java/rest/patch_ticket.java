package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class patch_ticket {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "TJxH9a5B";


        String server =
                "http://36.50.135.242:2230/api/rest/method";



        HttpClient client =
                HttpClient.newHttpClient();





        // =========================
        // PHASE 1: GET ticket
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
        // Lấy requestId + etag
        // =========================


        JSONObject json =
                new JSONObject(
                        getResponse.body()
                );



        String requestId =
                json.getString(
                        "requestId"
                );



        JSONObject data =
                json.getJSONObject(
                        "data"
                );



        String etag =
                data.getString(
                        "etag"
                );



        System.out.println(
                "requestId = "
                + requestId
        );


        System.out.println(
                "etag = "
                + etag
        );




        // =========================
        // PHASE 2: PATCH
        // =========================



        JSONObject answer =
                new JSONObject();



        answer.put(
                "status",
                "RESOLVED"
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





        String patchUrl =
                server
                + "/"
                + requestId;





        HttpRequest patchRequest =
                HttpRequest.newBuilder()

                .uri(
                        URI.create(patchUrl)
                )


                .header(
                        "Content-Type",
                        "application/json"
                )


                // BẮT BUỘC
                .header(
                        "If-Match",
                        etag
                )



                .method(
                        "PATCH",
                        HttpRequest.BodyPublishers
                        .ofString(
                                body.toString()
                        )
                )


                .build();







        HttpResponse<String> patchResponse =
                client.send(
                        patchRequest,
                        HttpResponse.BodyHandlers.ofString()
                );






        System.out.println(
                "PATCH RESPONSE:"
        );


        System.out.println(
                patchResponse.body()
        );



    }
}