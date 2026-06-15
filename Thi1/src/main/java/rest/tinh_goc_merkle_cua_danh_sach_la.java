package rest;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.List;



public class tinh_goc_merkle_cua_danh_sach_la {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "HQmfGsY2";


        String server =
                "http://36.50.135.242:2230/api/rest/header";



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



        JSONArray leaves =

                json.getJSONObject(
                        "data"
                )
                .getJSONArray(
                        "leaves"
                );







        // =========================
        // SHA-256 từng leaf
        // =========================


        List<byte[]> level =

                new ArrayList<>();




        for(int i = 0; i < leaves.length(); i++){


            String leaf =

                    leaves.getString(i);



            level.add(
                    sha256(
                            leaf.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    )
            );


        }







        // =========================
        // Xây Merkle Tree
        // =========================


        while(level.size() > 1){



            List<byte[]> next =

                    new ArrayList<>();




            for(int i = 0; i < level.size(); i += 2){



                byte[] left =
                        level.get(i);



                byte[] right;



                if(i + 1 < level.size()){


                    right =
                            level.get(i + 1);


                }

                else{


                    right =
                            left;

                }






                byte[] combined =

                        new byte[
                                left.length
                                +
                                right.length
                        ];





                System.arraycopy(
                        left,
                        0,
                        combined,
                        0,
                        left.length
                );



                System.arraycopy(
                        right,
                        0,
                        combined,
                        left.length,
                        right.length
                );







                next.add(
                        sha256(combined)
                );


            }



            level = next;


        }







        String answer =

                bytesToHex(
                        level.get(0)
                );






        System.out.println(
                "MERKLE ROOT = "
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

                        URI.create(

                                server + "/submit"

                        )

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






    // SHA-256 byte[]
    public static byte[] sha256(byte[] input)
            throws Exception {



        MessageDigest md =

                MessageDigest.getInstance(
                        "SHA-256"
                );


        return md.digest(input);

    }







    // byte[] -> hex lowercase
    public static String bytesToHex(byte[] bytes){



        StringBuilder sb =

                new StringBuilder();



        for(byte b : bytes){


            sb.append(

                    String.format(
                            "%02x",
                            b
                    )

            );

        }



        return sb.toString();


    }



}