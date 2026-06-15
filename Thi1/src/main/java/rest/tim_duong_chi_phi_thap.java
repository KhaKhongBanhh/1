package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class tim_duong_chi_phi_thap {
    static class Edge {
        String target;
        int weight;

        Edge(String target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    static class NodePair implements Comparable<NodePair> {
        String node;
        int distance;

        NodePair(String node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodePair o) {
            return Integer.compare(this.distance, o.distance);
        }
    }

    public static void main(String[] args) {
        String ma = "B22DCDT095"; 
        String qcode = "UKKZAmio"; 
        String url = "http://36.50.135.242:2230/api/rest/object"; 
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();
            String geturl = url + "?studentCode=" + ma + "&qCode=" + qcode;
            
            HttpRequest get = HttpRequest.newBuilder().uri(URI.create(geturl)).GET().build();
            HttpResponse<String> getres = client.send(get, HttpResponse.BodyHandlers.ofString());
            String getresBody = getres.body();
            
            JsonObject oj = gson.fromJson(getresBody, JsonObject.class);
            String rqid = oj.get("requestId").getAsString();
            JsonObject dataoj = oj.getAsJsonObject("data");
            
            String startNode = dataoj.get("start").getAsString();
            String endNode = dataoj.get("end").getAsString();
            JsonArray edgesArray = dataoj.getAsJsonArray("edges");

            Map<String, List<Edge>> adj = new HashMap<>();
            for (int i = 0; i < edgesArray.size(); i++) {
                JsonObject e = edgesArray.get(i).getAsJsonObject();
                String src = e.get("from").getAsString();
                String dest = e.get("to").getAsString();
                int weight = e.get("weight").getAsInt();

                adj.putIfAbsent(src, new ArrayList<>());
                adj.putIfAbsent(dest, new ArrayList<>());
                adj.get(src).add(new Edge(dest, weight));
            }

            Map<String, Integer> distances = new HashMap<>();
            Map<String, String> parents = new HashMap<>();
            PriorityQueue<NodePair> pq = new PriorityQueue<>();

            distances.put(startNode, 0);
            pq.add(new NodePair(startNode, 0));

            while (!pq.isEmpty()) {
                NodePair current = pq.poll();
                String u = current.node;
                int distU = current.distance;

                if (distU > distances.getOrDefault(u, Integer.MAX_VALUE)) {
                    continue;
                }

                if (u.equals(endNode)) {
                    break;
                }

                List<Edge> neighbors = adj.getOrDefault(u, new ArrayList<>());
                for (Edge edge : neighbors) {
                    String v = edge.target;
                    int weight = edge.weight;

                    if (distU + weight < distances.getOrDefault(v, Integer.MAX_VALUE)) {
                        distances.put(v, distU + weight);
                        parents.put(v, u);
                        pq.add(new NodePair(v, distU + weight));
                    }
                }
            }

            int finalCost = distances.getOrDefault(endNode, -1);
            
            List<String> path = new ArrayList<>();
            String curr = endNode;
            while (curr != null) {
                path.add(curr);
                curr = parents.get(curr);
            }
            Collections.reverse(path);

            String pathStr = String.join("->", path);
            String answer = finalCost + "|" + pathStr;
            System.out.println("Kết quả answer: " + answer);

            JsonObject submitBody = new JsonObject();
            submitBody.addProperty("studentCode", ma);
            submitBody.addProperty("qCode", qcode);
            submitBody.addProperty("requestId", rqid);
            submitBody.addProperty("answer", answer);

            String requestBodyString = gson.toJson(submitBody);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Phản hồi: " + postResponse.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}