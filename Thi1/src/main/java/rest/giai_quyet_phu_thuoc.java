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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class giai_quyet_phu_thuoc {
    
    static class Module implements Comparable<Module> {
        String name;
        int version;

        public Module(String fullName) {
            this.name = fullName;
            String[] parts = fullName.split("@v");
            if (parts.length > 1) {
                this.version = Integer.parseInt(parts[1]);
            } else {
                this.version = 0;
            }
        }

        @Override
        public int compareTo(Module other) {
            return Integer.compare(other.version, this.version);
        }
    }

    public static void main(String[] args) {
        String ma = "B22DCDT095";
        String qcode = "ChQiIBFU";
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

            JsonArray modulesArray = dataoj.getAsJsonArray("modules");
            JsonArray depsArray = dataoj.getAsJsonArray("deps");

            List<String> modules = new ArrayList<>();
            Map<String, Integer> inDegree = new HashMap<>();
            Map<String, List<String>> adj = new HashMap<>();

            for (int i = 0; i < modulesArray.size(); i++) {
                JsonElement element = modulesArray.get(i);
                String m;
                if (element.isJsonObject()) {
                    JsonObject mObj = element.getAsJsonObject();
                    m = mObj.has("name") ? mObj.get("name").getAsString() : mObj.get("id").getAsString();
                } else {
                    m = element.getAsString();
                }
                modules.add(m);
                inDegree.put(m, 0);
                adj.put(m, new ArrayList<>());
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

            List<Module> readyQueue = new ArrayList<>();
            for (String m : modules) {
                if (inDegree.get(m) == 0) {
                    readyQueue.add(new Module(m));
                }
            }
            Collections.sort(readyQueue);

            List<String> result = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                Module currModule = readyQueue.remove(0);
                result.add(currModule.name);

                List<String> neighbors = adj.get(currModule.name);
                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        readyQueue.add(new Module(neighbor));
                    }
                }
                Collections.sort(readyQueue);
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