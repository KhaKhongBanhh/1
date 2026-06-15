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



public class chong_gui_trung {



    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "ZD2wKV4H";



        String baseUrl =
                "http://36.50.135.242:2230/api/rest/method";



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





        JSONArray requests =

                data.getJSONArray(
                        "requests"
                );







        // =========================
        // LRU CACHE
        // chỉ lấy request đầu tiên
        // của mỗi key
        // =========================


        LinkedHashSet<String> cache =

                new LinkedHashSet<>();




        List<String> result =

                new ArrayList<>();






        for(int i = 0; i < requests.length(); i++){



            JSONObject req =

                    requests.getJSONObject(i);




            String key =

                    req.getString(
                            "key"
                    );




            String id =

                    req.getString(
                            "id"
                    );





            if(!cache.contains(key)){


                cache.add(key);


                result.add(id);



                // giới hạn LRU
                if(cache.size() > capacity){


                    Iterator<String> it =
                            cache.iterator();



                    it.next();


                    it.remove();


                }


            }



        }







        String answer =

                String.join(
                        ",",
                        result
                );





        System.out.println(
                "ANSWER = "
                +
                answer
        );







        // =========================
        // PUT PHASE 2
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
                "answer",
                answer
        );







        HttpRequest putRequest =

                HttpRequest.newBuilder()

                .uri(

                        URI.create(

                                baseUrl
                                +
                                "/"
                                +
                                requestId

                        )

                )


                .header(

                        "Content-Type",

                        "application/json"

                )


                .PUT(

                        HttpRequest.BodyPublishers.ofString(

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
                "PUT RESPONSE:"
        );



        System.out.println(
                putResponse.body()
        );



    }

}