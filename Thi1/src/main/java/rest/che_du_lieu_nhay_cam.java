package rest;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class che_du_lieu_nhay_cam {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "5dQpmxwr";


        String server =
                "http://36.50.135.242:2230/api/rest/character";


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



        System.out.println("GET:");

        System.out.println(
                getResponse.body()
        );





        // =========================
        // b. Lấy dữ liệu
        // =========================


        JSONObject json =
                new JSONObject(
                    getResponse.body()
                );


        String requestId =
                json.getString("requestId");


        String data =
                json.getString("data");





        // =========================
        // c. Mask dữ liệu
        // =========================


        String answer = data;



        // thay email

        answer =
        answer.replaceAll(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            "[EMAIL]"
        );



        // thay số điện thoại VN 10 số bắt đầu 0

        answer =
        answer.replaceAll(
            "\\b0\\d{9}\\b",
            "[PHONE]"
        );



        // thay token=value

        answer =
        answer.replaceAll(
            "token=[^\\s|]+",
            "token=[TOKEN]"
        );




        System.out.println("ANSWER:");

        System.out.println(answer);





        // =========================
        // d. POST submit
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





        System.out.println("POST:");

        System.out.println(
            postResponse.body()
        );



    }
}