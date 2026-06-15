package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.*;



public class chon_danh_sach_toi_uu {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "WnAdWWSh";


        String baseUrl =
                "http://36.50.135.242:2230/api/rest/object";


        String submitUrl =
                "http://36.50.135.242:2230/api/rest/object/submit";



        HttpClient client =
                HttpClient.newHttpClient();






        // =========================
        // PHASE 1 GET
        // =========================


        String url =

                baseUrl

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





        int capacity =

                data.getInt(
                        "capacity"
                );




        JSONArray items =

                data.getJSONArray(
                        "items"
                );






        // =========================
        // 0/1 Knapsack DP
        // =========================



        int n = items.length();



        int[] dp =

                new int[capacity + 1];



        List<Integer>[] choose =

                new ArrayList[capacity + 1];




        for(int i = 0; i <= capacity; i++){


            choose[i] =

                    new ArrayList<>();

        }






        for(int i = 0; i < n; i++){



            JSONObject item =

                    items.getJSONObject(i);




            int weight =

                    item.getInt(
                            "weight"
                    );



            int value =

                    item.getInt(
                            "value"
                    );






            String id =

                    item.getString(
                            "id"
                    );





            for(int w = capacity; w >= weight; w--){



                if(
                        dp[w-weight] + value
                        >
                        dp[w]
                ){



                    dp[w] =

                            dp[w-weight]
                            +
                            value;



                    choose[w] =

                            new ArrayList<>(
                                    choose[w-weight]
                            );



                    choose[w].add(i);



                }


            }


        }








        // =========================
        // Lấy item được chọn
        // =========================



        List<String> result =

                new ArrayList<>();




        for(Integer idx : choose[capacity]){


            result.add(

                    items.getJSONObject(idx)

                    .getString(
                            "id"
                    )

            );


        }





        String answer =

                String.join(
                        ",",
                        result
                )

                +

                "|"

                +

                dp[capacity];






        System.out.println(
                "ANSWER = "
                +
                answer
        );








        // =========================
        // POST
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
                        URI.create(submitUrl)
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