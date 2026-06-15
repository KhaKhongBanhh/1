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
import java.util.List;



public class chon_phan_tu_thu_k_theo_heap {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "p8sDwwRK";


        String server =
                "http://36.50.135.242:2230/api/rest/path";



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



        JSONArray records =
                data.getJSONArray(
                        "records"
                );



        int k =
                data.getInt(
                        "k"
                );



        String type =
                data.getString(
                        "type"
                );







        // =========================
        // Lọc theo type
        // =========================


        List<JSONObject> filtered =
                new ArrayList<>();





        for(int i = 0; i < records.length(); i++){


            JSONObject record =
                    records.getJSONObject(i);



            if(record.getString("type")
                    .equals(type)){


                filtered.add(record);


            }

        }







        // =========================
        // Sắp xếp giảm dần value
        // =========================


        filtered.sort(
                (a,b) ->

                        Integer.compare(
                                b.getInt("value"),
                                a.getInt("value")
                        )

        );







        // =========================
        // Chọn phần tử thứ k
        // =========================


        JSONObject selected;



        if(k <= filtered.size()){


            selected =
                    filtered.get(k - 1);


        }

        else{


            selected =
                    filtered.get(
                            filtered.size() - 1
                    );

        }







        String answer =

                selected.getString(
                        "id"
                )

                + "|"

                + selected.getInt(
                        "value"
                );






        System.out.println(
                "ANSWER = "
                + answer
        );








        // =========================
        // PHASE 2
        // GET /submit
        // =========================


        String submitUrl =

                server

                + "/submit"

                + "?studentCode="

                + URLEncoder.encode(
                        studentCode,
                        StandardCharsets.UTF_8
                )


                + "&qCode="

                + URLEncoder.encode(
                        qCode,
                        StandardCharsets.UTF_8
                )


                + "&requestId="

                + URLEncoder.encode(
                        requestId,
                        StandardCharsets.UTF_8
                )


                + "&answer="

                + URLEncoder.encode(
                        answer,
                        StandardCharsets.UTF_8
                );







        HttpRequest submitRequest =
                HttpRequest.newBuilder()

                .uri(
                        URI.create(submitUrl)
                )

                .GET()

                .build();







        HttpResponse<String> submitResponse =
                client.send(
                        submitRequest,
                        HttpResponse.BodyHandlers.ofString()
                );






        System.out.println(
                "SUBMIT RESPONSE:"
        );


        System.out.println(
                submitResponse.body()
        );



    }

}