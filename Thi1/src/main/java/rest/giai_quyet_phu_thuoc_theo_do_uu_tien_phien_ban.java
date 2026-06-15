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



public class giai_quyet_phu_thuoc_theo_do_uu_tien_phien_ban {


    static class Module {

        String id;
        int version;


        Module(String id, int version){

            this.id = id;
            this.version = version;

        }

    }



    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "W18bkqvd";



        String baseUrl =
                "http://36.50.135.242:2230/api/rest/method";



        HttpClient client =
                HttpClient.newHttpClient();





        // =========================
        // GET PHASE 1
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







        // =========================
        // MODULES
        // =========================


        JSONArray modules =

                data.getJSONArray(
                        "modules"
                );




        int n = modules.length();



        Map<String,Integer> index =

                new HashMap<>();



        List<Module> list =

                new ArrayList<>();





        for(int i = 0; i < n; i++){



            JSONObject m =

                    modules.getJSONObject(i);




            String id =

                    m.getString(
                            "id"
                    );



            int version =

                    m.getInt(
                            "version"
                    );




            index.put(
                    id,
                    i
            );



            list.add(
                    new Module(
                            id,
                            version
                    )
            );


        }








        // =========================
        // GRAPH
        // =========================


        List<List<Integer>> graph =

                new ArrayList<>();



        for(int i = 0; i < n; i++){


            graph.add(
                    new ArrayList<>()
            );

        }



        int[] indegree =

                new int[n];






        JSONArray deps =

                data.getJSONArray(
                        "deps"
                );






        for(int i = 0; i < deps.length(); i++){



            JSONObject d =

                    deps.getJSONObject(i);



            String before =

                    d.getString(
                            "before"
                    );



            String after =

                    d.getString(
                            "after"
                    );



            int u =

                    index.get(before);



            int v =

                    index.get(after);





            graph.get(u).add(v);



            indegree[v]++;

        }







        // =========================
        // Priority Queue
        // version cao trước
        // =========================


        PriorityQueue<Integer> pq =

                new PriorityQueue<>(

                        (a,b)->

                                Integer.compare(

                                        list.get(b).version,

                                        list.get(a).version

                                )

                );






        for(int i = 0; i < n; i++){


            if(indegree[i] == 0){


                pq.add(i);


            }


        }







        // =========================
        // KAHN
        // =========================


        List<String> answerList =

                new ArrayList<>();





        while(!pq.isEmpty()){



            int cur =

                    pq.poll();




            answerList.add(

                    list.get(cur).id

            );






            for(int next : graph.get(cur)){



                indegree[next]--;



                if(indegree[next] == 0){



                    pq.add(next);


                }


            }



        }






        String answer =

                String.join(
                        ",",
                        answerList
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