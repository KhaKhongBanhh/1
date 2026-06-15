package rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class trung_vi_truot{
	public static void main(String[] args) {
		String ma = "B22DCDT095";
		String qcode = "i9sci3wQ";
		String url = "http://36.50.135.242:2230/api/rest/data";
		
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
			JsonArray val = dataoj.getAsJsonArray("values");
			int window = dataoj.get("windowSize").getAsInt();

            List<Double> listvalues = new ArrayList<>();
            for (int i = 0; i < val.size(); i++) {
                listvalues.add(val.get(i).getAsDouble());
            }

            List<String> medianResults = new ArrayList<>();
            for (int i = 0; i <= listvalues.size() - window; i++) {
                List<Double> listwindow = new ArrayList<>(listvalues.subList(i, i + window));
                Collections.sort(listwindow);
                
                double median;
                int mid = window / 2;
                if (window % 2 == 0) {
                    median = (listwindow.get(mid - 1) + listwindow.get(mid)) / 2.0;
                } else {
                    median = listwindow.get(mid);
                }
         
                medianResults.add(String.format("%.2f", median)); 
            }

            String answer = String.join(", ", medianResults);
            answer = answer.replace(" ", ""); 
            //String ans = "J1,J4,J5,J6";
            System.out.println("Kết quả chuỗi trung vị tính được: " + answer);

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
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
