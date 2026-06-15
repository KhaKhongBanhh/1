package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;



public class chon_ban_ghi_bang_binary_search {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "L6qaxExl";


        String baseUrl =
                "http://36.50.135.242:2230/api/rest/path";


        String submitUrl =
                "http://36.50.135.242:2230/api/rest/path/submit";



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







        // =========================
        // Đọc JSON
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



        JSONArray records =

                data.getJSONArray(
                        "records"
                );



        int target =

                data.getInt(
                        "target"
                );







        // =========================
        // Binary Search
        // tìm threshold đầu tiên >= target
        // =========================


        int left = 0;

        int right = records.length() - 1;


        int index = -1;



        while(left <= right){


            int mid =
                    (left + right) / 2;



            JSONObject item =

                    records.getJSONObject(mid);



            int threshold =

                    item.getInt(
                            "threshold"
                    );




            if(threshold >= target){


                index = mid;


                right = mid - 1;


            }
            else{


                left = mid + 1;


            }


        }






        String answer = "";



        if(index != -1){


            answer =

                    records.getJSONObject(index)

                    .getString(
                            "id"
                    );

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