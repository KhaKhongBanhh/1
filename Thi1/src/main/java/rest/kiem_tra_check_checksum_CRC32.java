package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.zip.CRC32;



public class kiem_tra_check_checksum_CRC32 {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "GQ0tmTzT";


        String server =
                "http://36.50.135.242:2230/api/rest/header";



        HttpClient client =
                HttpClient.newHttpClient();







        // =========================
        // PHASE 1 GET
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
        // Lấy requestId + payload
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



        String payload =

                data.getString(
                        "payload"
                );







        // =========================
        // CRC32
        // =========================


        CRC32 crc32 = new CRC32();



        crc32.update(
                payload.getBytes(
                        StandardCharsets.UTF_8
                )
        );



        String answer =

                Long.toHexString(
                        crc32.getValue()
                ).toLowerCase();






        System.out.println(
                "CRC32 = "
                + answer
        );








        // =========================
        // PHASE 2 POST
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

                        HttpRequest.BodyPublishers.ofString(

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