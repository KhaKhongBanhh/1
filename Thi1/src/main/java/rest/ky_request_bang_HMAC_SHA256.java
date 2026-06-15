package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;



public class ky_request_bang_HMAC_SHA256 {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "YqKbcJ3j";


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
        // Lấy dữ liệu
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



        String nonce =

                data.getString(
                        "nonce"
                );



        String signingKey =

                data.getString(
                        "signingKey"
                );



        JSONArray events =

                data.getJSONArray(
                        "events"
                );







        // =========================
        // Tạo payload
        // nonce:event1|event2|...:STUDENT_CODE_UPPER
        // =========================


        StringBuilder payload =

                new StringBuilder();



        payload.append(
                nonce
        );



        payload.append(":");



        for(int i = 0; i < events.length(); i++){


            if(i > 0){

                payload.append("|");

            }


            payload.append(
                    events.getString(i)
            );

        }



        payload.append(":");



        payload.append(
                studentCode.toUpperCase()
        );





        String message =

                payload.toString();





        System.out.println(
                "PAYLOAD = "
                + message
        );








        // =========================
        // HMAC SHA256
        // =========================


        Mac mac =

                Mac.getInstance(
                        "HmacSHA256"
                );



        SecretKeySpec keySpec =

                new SecretKeySpec(

                        signingKey.getBytes(
                                StandardCharsets.UTF_8
                        ),

                        "HmacSHA256"

                );



        mac.init(
                keySpec
        );



        byte[] hash =

                mac.doFinal(

                        message.getBytes(
                                StandardCharsets.UTF_8
                        )

                );





        StringBuilder hex =

                new StringBuilder();




        for(byte b : hash){


            hex.append(

                    String.format(
                            "%02x",
                            b
                    )

            );

        }



        String answer =

                hex.toString();






        System.out.println(
                "SIGNATURE = "
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