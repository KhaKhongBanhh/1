package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class mo_phong_may_trang_thai {
    
    static class Transition {
        String fromState;
        String event;
        String toState;

        public Transition(String fromState, String event, String toState) {
            this.fromState = fromState;
            this.event = event;
            this.toState = toState;
        }
    }

    public static void main(String[] args) {
        String ma = "B22DCDT095";
        String qcode = "wDlIwvCG";
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

            String currentState = dataoj.get("initialState").getAsString();

            JsonArray eventsArray = dataoj.getAsJsonArray("events");
            List<String> eventsList = new ArrayList<>();
            for (int i = 0; i < eventsArray.size(); i++) {
                eventsList.add(eventsArray.get(i).getAsString());
            }

            JsonArray transitionsArray = dataoj.getAsJsonArray("transitions");
            List<Transition> transitionsList = new ArrayList<>();
            for (int i = 0; i < transitionsArray.size(); i++) {
                JsonObject tObj = transitionsArray.get(i).getAsJsonObject();
                transitionsList.add(new Transition(
                    tObj.get("from").getAsString(),
                    tObj.get("event").getAsString(),
                    tObj.get("to").getAsString()
                ));
            }

            for (String event : eventsList) {
                for (Transition t : transitionsList) {
                    if (t.fromState.equals(currentState) && t.event.equals(event)) {
                        currentState = t.toState;
                        break;
                    }
                }
            }

            JsonObject submitBody = new JsonObject();
            submitBody.addProperty("studentCode", ma);
            submitBody.addProperty("qCode", qcode);
            submitBody.addProperty("answer", currentState);

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