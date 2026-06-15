package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class path_parameter_query_parameter {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "r58asEuB";


        String server =
                "http://36.50.135.242:2230/api/rest/path";



        HttpClient client =
                HttpClient.newHttpClient();



        // =========================
        // PHASE 1: GET danh sách sản phẩm
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



        System.out.println("PHASE 1 RESPONSE:");

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
                json.getString("requestId");



        JSONArray products =
                json.getJSONArray("data");




        // =========================
        // Chọn productId hợp lệ
        // id là Integer nên dùng getInt()
        // =========================


        int productId =
                products
                .getJSONObject(0)
                .getInt("id");



        System.out.println(
                "Selected productId = "
                + productId
        );






        // =========================
        // PHASE 2:
        // GET /path/{productId}
        // =========================


        String url2 =
                server
                + "/"
                + productId

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

                + "&currency=USD";





        System.out.println("PHASE 2 URL:");

        System.out.println(url2);





        HttpRequest phase2Request =
                HttpRequest.newBuilder()

                .uri(
                        URI.create(url2)
                )

                .GET()

                .build();






        HttpResponse<String> phase2Response =
                client.send(
                        phase2Request,
                        HttpResponse.BodyHandlers.ofString()
                );





        System.out.println("PHASE 2 RESPONSE:");

        System.out.println(
                phase2Response.body()
        );



    }
}