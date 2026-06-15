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
public class chu_ky_HMAC {
	public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "OzA0X0Tj";


        String server =
                "http://36.50.135.242:2230/api/rest/header";



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



        JSONArray events =
                data.getJSONArray(
                        "events"
                );



        String signingKey =
                data.getString(
                        "signingKey"
                );






        // =========================
        // Tạo payload
        // nonce:event1|event2:STUDENT_CODE_UPPER
        // =========================



        StringBuilder eventText =
                new StringBuilder();



        for(int i = 0; i < events.length(); i++){


            if(i > 0){
                eventText.append("|");
            }


            eventText.append(
                    events.getString(i)
            );

        }




        String payload =
                nonce
                + ":"
                + eventText
                + ":"
                + studentCode.toUpperCase();





        System.out.println(
                "PAYLOAD:"
        );

        System.out.println(payload);







        // =========================
        // HMAC SHA256
        // =========================



        String signature =
                hmacSHA256(
                        payload,
                        signingKey
                );




        System.out.println(
                "SIGNATURE:"
        );

        System.out.println(signature);







        // =========================
        // PHASE 2: POST
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


                // gửi chữ ký ở HEADER
                .header(
                        "X-Signature",
                        signature
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






    // =========================
    // Hàm HMAC SHA256
    // =========================


    public static String hmacSHA256(
            String data,
            String key
    ) throws Exception {



        Mac mac =
                Mac.getInstance(
                        "HmacSHA256"
                );



        SecretKeySpec secretKey =
                new SecretKeySpec(
                        key.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );



        mac.init(secretKey);



        byte[] hash =
                mac.doFinal(
                        data.getBytes(
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



        return hex.toString();

    }

}
