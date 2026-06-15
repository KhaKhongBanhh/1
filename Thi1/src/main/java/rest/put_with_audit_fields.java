package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class put_with_audit_fields {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "uLsWUIhk";


        String server =
                "http://36.50.135.242:2230/api/rest/method";



        HttpClient client =
                HttpClient.newHttpClient();





        // =========================
        // PHASE 1: GET
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



        System.out.println("PHASE 1 RESPONSE:");

        System.out.println(
                getResponse.body()
        );






        // =========================
        // Lấy requestId
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



        System.out.println(
                "accountId = "
                + data.get("accountId")
        );


        System.out.println(
                "currentStatus = "
                + data.get("currentStatus")
        );


        System.out.println(
                "riskLevel = "
                + data.get("riskLevel")
        );


        System.out.println(
                "requiresAudit = "
                + data.get("requiresAudit")
        );







        // =========================
        // PHASE 2: PUT
        // =========================



        JSONObject answer =
                new JSONObject();



        answer.put(
                "status",
                "ACTIVE"
        );


        answer.put(
                "activatedBy",
                studentCode.toUpperCase()
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
                        URI.create(putUrl)
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
                "PHASE 2 RESPONSE:"
        );


        System.out.println(
                putResponse.body()
        );



    }
}