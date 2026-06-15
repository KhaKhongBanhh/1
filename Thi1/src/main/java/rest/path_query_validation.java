package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
public class path_query_validation {
	public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "76BGIUn4";


        String server =
                "http://36.50.135.242:2230/api/rest/path";



        HttpClient client =
                HttpClient.newHttpClient();





        // =========================
        // PHASE 1 - GET invoices
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
                json.getString(
                        "requestId"
                );



        JSONArray invoices =
                json.getJSONArray(
                        "data"
                );






        // =========================
        // Chọn invoiceId hợp lệ
        // id có thể là số
        // =========================


        int invoiceId =
                invoices
                .getJSONObject(0)
                .getInt("id");



        System.out.println(
                "Selected invoiceId = "
                + invoiceId
        );







        // =========================
        // PHASE 2
        // GET /path/{invoiceId}
        // =========================



        String url2 =
                server
                + "/"
                + invoiceId

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






        System.out.println(
                "PHASE 2 URL:"
        );

        System.out.println(url2);






        HttpRequest request2 =
                HttpRequest.newBuilder()

                .uri(
                        URI.create(url2)
                )

                .GET()

                .build();






        HttpResponse<String> response2 =
                client.send(
                        request2,
                        HttpResponse.BodyHandlers.ofString()
                );






        System.out.println(
                "PHASE 2 RESPONSE:"
        );


        System.out.println(
                response2.body()
        );



    }
}
