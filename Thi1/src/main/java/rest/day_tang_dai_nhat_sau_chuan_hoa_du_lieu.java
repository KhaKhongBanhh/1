package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;



public class day_tang_dai_nhat_sau_chuan_hoa_du_lieu {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "c6ao50xK";


        String server =
                "http://36.50.135.242:2230/api/rest/data";



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
        // Lấy requestId + values
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



        JSONArray valuesJson =
                data.getJSONArray(
                        "values"
                );







        // =========================
        // Patience Sorting LIS
        // =========================


        ArrayList<Integer> tails =
                new ArrayList<>();





        for(int i = 0; i < valuesJson.length(); i++){


            int x =
                    valuesJson.getInt(i);





            int left = 0;

            int right = tails.size();





            while(left < right){


                int mid =
                        (left + right) / 2;



                if(tails.get(mid) < x){

                    left = mid + 1;

                }

                else{

                    right = mid;

                }

            }





            if(left == tails.size()){


                tails.add(x);

            }

            else{


                tails.set(
                        left,
                        x
                );

            }


        }





        int answer =
                tails.size();





        System.out.println(
                "LIS length = "
                + answer
        );







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