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



public class lap_ke_hoach_ghe_diem {


    static int n;

    static int[][] dist;

    static String[] nodeName;

    static int[][] memo;

    static int[][] choice;





    static int dfs(
            int pos,
            int mask,
            int end,
            int fullMask
    ){


        if(mask == fullMask){

            return dist[pos][end];

        }




        if(memo[pos][mask] != -1){

            return memo[pos][mask];

        }



        int best = Integer.MAX_VALUE;



        for(int i = 0; i < n; i++){



            if(
                (fullMask & (1 << i)) != 0
                &&
                (mask & (1 << i)) == 0
            ){



                int cost =

                        dist[pos][i]

                        +

                        dfs(
                                i,
                                mask | (1 << i),
                                end,
                                fullMask
                        );




                if(cost < best){


                    best = cost;

                    choice[pos][mask] = i;


                }


            }

        }



        memo[pos][mask] = best;


        return best;

    }







    public static void main(String[] args) throws Exception {



        String studentCode = "B22DCAT108";

        String qCode = "Bq7hUT5w";


        String baseUrl =
                "http://36.50.135.242:2230/api/rest/path";


        String submitUrl =
                "http://36.50.135.242:2230/api/rest/path/submit";



        HttpClient client =
                HttpClient.newHttpClient();







        // ======================
        // GET
        // ======================


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






        HttpResponse<String> response =

                client.send(

                        getRequest,

                        HttpResponse.BodyHandlers.ofString()

                );





        System.out.println("GET RESPONSE:");

        System.out.println(response.body());







        JSONObject json =

                new JSONObject(
                        response.body()
                );



        String requestId =

                json.getString(
                        "requestId"
                );



        JSONObject data =

                json.getJSONObject(
                        "data"
                );








        // ======================
        // Nodes
        // ======================


        JSONArray nodes =

                data.getJSONArray(
                        "nodes"
                );



        n = nodes.length();



        nodeName = new String[n];



        Map<String,Integer> map =

                new HashMap<>();




        for(int i = 0; i < n; i++){


            nodeName[i] =
                    nodes.getString(i);



            map.put(
                    nodeName[i],
                    i
            );


        }








        // ======================
        // Edges
        // ======================


        dist = new int[n][n];



        for(int i = 0; i < n; i++){


            Arrays.fill(
                    dist[i],
                    999999
            );


            dist[i][i] = 0;

        }






        JSONArray edges =

                data.getJSONArray(
                        "edges"
                );





        for(int i = 0; i < edges.length(); i++){



            JSONObject e =

                    edges.getJSONObject(i);




            int from =

                    map.get(
                            e.getString("from")
                    );



            int to =

                    map.get(
                            e.getString("to")
                    );



            int weight =

                    e.getInt(
                            "weight"
                    );



            dist[from][to] =

                    Math.min(
                            dist[from][to],
                            weight
                    );



            dist[to][from] =

                    Math.min(
                            dist[to][from],
                            weight
                    );


        }








        // ======================
        // Floyd Warshall
        // ======================


        for(int k = 0; k < n; k++){


            for(int i = 0; i < n; i++){


                for(int j = 0; j < n; j++){


                    if(
                        dist[i][k]
                        +
                        dist[k][j]
                        <
                        dist[i][j]
                    ){


                        dist[i][j] =

                                dist[i][k]
                                +
                                dist[k][j];


                    }


                }


            }


        }








        // ======================
        // start end mandatory
        // ======================


        int start =

                map.get(
                        data.getString("start")
                );



        int end =

                map.get(
                        data.getString("end")
                );





        JSONArray mandatory =

                data.getJSONArray(
                        "mandatory"
                );




        int fullMask = 0;



        for(int i = 0; i < mandatory.length(); i++){



            fullMask |=

                    1 << map.get(

                            mandatory.getString(i)

                    );

        }



        fullMask |= 1 << start;









        // ======================
        // DP
        // ======================


        memo =

                new int[n][1 << n];


        choice =

                new int[n][1 << n];




        for(int[] row : memo){


            Arrays.fill(
                    row,
                    -1
            );

        }





        int cost =

                dfs(
                        start,
                        1 << start,
                        end,
                        fullMask
                );









        // ======================
        // Rebuild path
        // ======================


        List<String> path =

                new ArrayList<>();



        int cur = start;

        int mask = 1 << start;



        path.add(
                nodeName[cur]
        );




        while(cur != end){



            int next =

                    choice[cur][mask];



            cur = next;



            mask |=

                    1 << cur;



            path.add(
                    nodeName[cur]
            );


        }






        String route =

                String.join(
                        "->",
                        path
                );




        String answer =

                cost
                +
                "|"
                +
                route;





        System.out.println(
                "ANSWER = "
                + answer
        );








        // ======================
        // POST
        // ======================


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
                        URI.create(submitUrl)
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






        HttpResponse<String> post =

                client.send(

                        postRequest,

                        HttpResponse.BodyHandlers.ofString()

                );





        System.out.println(
                "POST RESPONSE:"
        );


        System.out.println(
                post.body()
        );


    }

}