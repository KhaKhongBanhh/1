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



public class sap_xep_chuoi_phu_thuoc_theo_topo {


    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "ChF9sin2";



        String baseUrl =
                "http://36.50.135.242:2230/api/rest/method";



        HttpClient client =
                HttpClient.newHttpClient();





        // =========================
        // PHASE 1 GET
        // =========================


        String url =

                baseUrl
                +
                "?studentCode="
                +
                URLEncoder.encode(
                        studentCode,
                        StandardCharsets.UTF_8
                )
                +
                "&qCode="
                +
                URLEncoder.encode(
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
        // TASKS
        // =========================


        JSONArray tasks =

                data.getJSONArray(
                        "tasks"
                );



        int n = tasks.length();




        Map<String,Integer> map =

                new HashMap<>();



        List<String> list =

                new ArrayList<>();





        for(int i = 0; i < n; i++){


            String t =

                    tasks.getString(i);



            map.put(
                    t,
                    i
            );


            list.add(t);


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



            JSONObject dep =

                    deps.getJSONObject(i);




            // sửa ở đây
            String before =

                    dep.getString(
                            "before"
                    );



            String after =

                    dep.getString(
                            "after"
                    );





            int u =

                    map.get(before);



            int v =

                    map.get(after);






            graph.get(u).add(v);



            indegree[v]++;



        }









        // =========================
        // KAHN TOPO SORT
        // =========================



        Queue<Integer> queue =

                new LinkedList<>();






        for(int i = 0; i < n; i++){



            if(indegree[i] == 0){


                queue.add(i);


            }


        }






        List<String> answerList =

                new ArrayList<>();






        while(!queue.isEmpty()){



            int cur =

                    queue.poll();




            answerList.add(

                    list.get(cur)

            );






            for(int next : graph.get(cur)){



                indegree[next]--;




                if(indegree[next] == 0){



                    queue.add(next);


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