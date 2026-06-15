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



public class top_k_ma_giao_dich_xuat_hien_nhieu_nhat {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "nYATSg2t";


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



        JSONArray codes =
                data.getJSONArray(
                        "codes"
                );



        int k =
                data.getInt(
                        "k"
                );








        // =========================
        // Đếm tần suất
        // =========================


        Map<String,Integer> frequency =
                new HashMap<>();



        for(int i = 0; i < codes.length(); i++){


            String code =
                    codes.getString(i);



            frequency.put(
                    code,
                    frequency.getOrDefault(code,0) + 1
            );

        }







        // =========================
        // Sắp xếp:
        // frequency giảm
        // nếu bằng nhau code tăng
        // =========================


        List<String> sortedCodes =
                new ArrayList<>(
                        frequency.keySet()
                );




        Collections.sort(
                sortedCodes,
                new Comparator<String>() {


                    public int compare(
                            String a,
                            String b
                    ){


                        int countCompare =
                                frequency.get(b)
                                -
                                frequency.get(a);



                        if(countCompare != 0){

                            return countCompare;

                        }



                        return a.compareTo(b);

                    }

                }

        );







        // =========================
        // Lấy k mã
        // =========================


        StringBuilder answer =
                new StringBuilder();




        for(int i = 0; i < k; i++){


            if(i > 0){

                answer.append("|");

            }



            String code =
                    sortedCodes.get(i);



            answer.append(
                    code
            );


            answer.append("=");


            answer.append(
                    frequency.get(code)
            );


        }






        System.out.println(
                "ANSWER = "
                + answer
        );







        // =========================
        // PHASE 2: POST submit
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
                answer.toString()
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
