package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class sap_xep_chuoi_theo_topo {
    public static void main(String[] args) {
        String ma = "B22DCDT095";
        String qcode = "kZzZDJNJ";
        String url = "http://36.50.135.242:2230/api/rest/method";

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

            JsonArray tasksArray = dataoj.getAsJsonArray("tasks");
            JsonArray depsArray = dataoj.getAsJsonArray("deps");

            List<String> tasks = new ArrayList<>();
            Map<String, Integer> inDegree = new HashMap<>();
            Map<String, List<String>> adj = new HashMap<>();

            for (int i = 0; i < tasksArray.size(); i++) {
                String task = tasksArray.get(i).getAsString();
                tasks.add(task);
                inDegree.put(task, 0);
                adj.put(task, new ArrayList<>());
            }

            for (int i = 0; i < depsArray.size(); i++) {
                JsonObject depObj = depsArray.get(i).getAsJsonObject();
                String from = depObj.get("from").getAsString();
                String to = depObj.get("to").getAsString();

                if (adj.containsKey(from) && adj.containsKey(to)) {
                    adj.get(from).add(to);
                    inDegree.put(to, inDegree.get(to) + 1);
                }
            }

            Queue<String> queue = new LinkedList<>();
            Collections.sort(tasks);
            for (String task : tasks) {
                if (inDegree.get(task) == 0) {
                    queue.add(task);
                }
            }

            List<String> result = new ArrayList<>();
            while (!queue.isEmpty()) {
                List<String> currentLevel = new ArrayList<>();
                while (!queue.isEmpty()) {
                    currentLevel.add(queue.poll());
                }
                Collections.sort(currentLevel);

                String curr = currentLevel.remove(0);
                result.add(curr);

                for (String next : currentLevel) {
                    ((LinkedList<String>) queue).addFirst(next);
                }

                List<String> neighbors = adj.get(curr);
                List<String> nextAvailable = new ArrayList<>();
                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        nextAvailable.add(neighbor);
                    }
                }
                Collections.sort(nextAvailable);
                queue.addAll(nextAvailable);
            }

            String answer = String.join(",", result);

            JsonObject submitBody = new JsonObject();
            submitBody.addProperty("studentCode", ma);
            submitBody.addProperty("qCode", qcode);
            submitBody.addProperty("answer", answer);

            String requestBodyString = gson.toJson(submitBody);
            String puturl = url + "/" + rqid;

            HttpRequest putRequest = HttpRequest.newBuilder()
                    .uri(URI.create(puturl))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Phản hồi: " + putResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}