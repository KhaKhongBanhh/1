package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.ArrayDeque;
import java.util.Deque;



public class tong_lon_nhat_cua_cua_so_con_hop_le {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "yaVChDby";


        String server =
                "http://36.50.135.242:2230/api/rest/data";



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
        // Lấy requestId + data
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



        int k =
                data.getInt(
                        "k"
                );





        int n =
                valuesJson.length();



        int[] values =
                new int[n];




        for(int i = 0; i < n; i++){


            values[i] =
                    valuesJson.getInt(i);

        }







        // =========================
        // Prefix sum + Deque
        // =========================



        long[] prefix =
                new long[n + 1];



        for(int i = 0; i < n; i++){


            prefix[i + 1] =
                    prefix[i]
                    + values[i];

        }






        Deque<Integer> deque =
                new ArrayDeque<>();



        long maxSum =
                Long.MIN_VALUE;





        for(int i = 0; i <= n; i++){



            // bỏ prefix quá xa
            while(
                    !deque.isEmpty()
                    &&
                    deque.peekFirst() < i - k
            ){

                deque.pollFirst();

            }





            // tính đáp án
            if(!deque.isEmpty()){


                long current =
                        prefix[i]
                        -
                        prefix[deque.peekFirst()];


                if(current > maxSum){

                    maxSum = current;

                }

            }






            // giữ prefix nhỏ nhất
            while(
                    !deque.isEmpty()
                    &&
                    prefix[deque.peekLast()]
                    >=
                    prefix[i]
            ){

                deque.pollLast();

            }



            deque.addLast(i);


        }





        System.out.println(
                "ANSWER = "
                + maxSum
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
                maxSum
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