package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class chon_bao_gia_van_chuyen_sla {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "uUoDd4li";


        String server =
                "http://36.50.135.242:2230/api/rest/object";



        HttpClient client =
                HttpClient.newHttpClient();




        // =========================
        // a. GET
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
                .uri(URI.create(url))
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
        // b. Parse JSON
        // =========================


        JSONObject response =
                new JSONObject(
                    getResponse.body()
                );



        String requestId =
                response.getString(
                    "requestId"
                );



        JSONObject data =
                response.getJSONObject(
                    "data"
                );



        double weightKg =
                data.getDouble(
                    "weightKg"
                );



        int maxEtaDays =
                data.getInt(
                    "maxEtaDays"
                );



        JSONArray quotes =
                data.getJSONArray(
                    "quotes"
                );






        // =========================
        // c,d. Chọn quote tốt nhất
        // =========================


        JSONObject bestQuote = null;


        double minFee = Double.MAX_VALUE;


        double bestReliability = -1;


        double bestTotalFee = 0;



        for(int i = 0; i < quotes.length(); i++){


            JSONObject quote =
                    quotes.getJSONObject(i);



            int etaDays =
                    quote.getInt(
                        "etaDays"
                    );



            // bỏ quote quá thời gian

            if(etaDays > maxEtaDays){
                continue;
            }



            double baseFee =
                    quote.getDouble(
                        "baseFee"
                    );


            double perKgFee =
                    quote.getDouble(
                        "perKgFee"
                    );



            double reliability =
                    quote.getDouble(
                        "reliability"
                    );



            double totalFee =
                    baseFee
                    + weightKg * perKgFee;



            totalFee =
                    Math.round(
                        totalFee * 100
                    ) / 100.0;




            if(
                totalFee < minFee
                ||
                (
                 totalFee == minFee
                 &&
                 reliability > bestReliability
                )
            ){


                minFee = totalFee;

                bestReliability = reliability;

                bestTotalFee = totalFee;

                bestQuote = quote;

            }


        }




        String carrier =
                bestQuote.getString(
                    "carrier"
                );


        int etaDays =
                bestQuote.getInt(
                    "etaDays"
                );



        System.out.println(
            "carrier = " + carrier
        );

        System.out.println(
            "totalFee = " + bestTotalFee
        );

        System.out.println(
            "etaDays = " + etaDays
        );







        // =========================
        // e. POST submit
        // =========================


        JSONObject answer =
                new JSONObject();



        answer.put(
                "carrier",
                carrier
        );


        answer.put(
                "totalFee",
                bestTotalFee
        );


        answer.put(
                "etaDays",
                etaDays
        );




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