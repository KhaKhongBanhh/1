package rest;


import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;



public class phat_hien_chuoi_con_lap_bang_rolling_hash {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "vzEyxovC";


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



        String text =

                data.getString(
                        "text"
                );



        int windowSize =

                data.getInt(
                        "windowSize"
                );







        // =========================
        // Rolling Hash
        // =========================


        String answer = "NONE";



        long base = 257;

        long mod = 1000000007;



        long power = 1;



        for(int i = 1; i < windowSize; i++){


            power =
                    (power * base)
                    % mod;

        }







        Map<Long, String> seen =
                new HashMap<>();



        long hash = 0;







        for(int i = 0; i < text.length(); i++){



            hash =

                    (hash * base
                    + text.charAt(i))
                    % mod;







            // loại ký tự cũ khỏi cửa sổ


            if(i >= windowSize){


                hash -=

                        (text.charAt(i - windowSize)
                        * power)
                        % mod;



                if(hash < 0){

                    hash += mod;

                }

            }







            // đủ độ dài cửa sổ


            if(i >= windowSize - 1){



                int start =

                        i - windowSize + 1;





                if(seen.containsKey(hash)){


                    answer =

                            text.substring(
                                    start,
                                    start + windowSize
                            );


                    break;

                }





                seen.put(

                        hash,

                        text.substring(
                                start,
                                start + windowSize
                        )

                );


            }

        }








        System.out.println(
                "ANSWER = "
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