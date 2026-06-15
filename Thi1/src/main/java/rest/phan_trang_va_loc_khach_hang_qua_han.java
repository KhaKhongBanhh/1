package rest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class phan_trang_va_loc_khach_hang_qua_han {


    public static void main(String[] args) throws Exception {


        String studentCode = "B22DCAT108";

        String qCode = "mHx5bEU6";


        String server =
                "http://36.50.135.242:2230/api/rest/path";



        HttpClient client =
                HttpClient.newHttpClient();




        // =========================
        // PHASE 1: GET danh sách khách hàng
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



        JSONArray customers =
                json.getJSONArray(
                        "data"
                );







        // =========================
        // Chọn khách hàng OVERDUE
        // overdueAmount lớn nhất
        // =========================


        JSONObject selectedCustomer = null;


        double maxOverdue = -1;





        for(int i = 0; i < customers.length(); i++){


            JSONObject customer =
                    customers.getJSONObject(i);



            String status =
                    customer.getString(
                            "status"
                    );



            if(status.equals("OVERDUE")){


                double overdueAmount =
                        customer.getDouble(
                                "overdueAmount"
                        );



                if(overdueAmount > maxOverdue){


                    maxOverdue = overdueAmount;


                    selectedCustomer = customer;

                }

            }

        }






        // customerId là String (CUS-xxxx)
        String customerId =
                selectedCustomer.getString(
                        "customerId"
                );



        int page =
                selectedCustomer.getInt(
                        "page"
                );





        System.out.println(
                "Selected customerId = "
                + customerId
        );


        System.out.println(
                "Selected page = "
                + page
        );


        System.out.println(
                "Max overdueAmount = "
                + maxOverdue
        );







        // =========================
        // PHASE 2:
        // GET /path/{customerId}
        // =========================



        String url2 =
                server
                + "/"
                + customerId

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

                + "&status=OVERDUE"

                + "&page="
                + page;







        System.out.println(
                "PHASE 2 URL:"
        );


        System.out.println(
                url2
        );







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







        System.out.println(
                "PHASE 2 RESPONSE:"
        );


        System.out.println(
                phase2Response.body()
        );



    }
}