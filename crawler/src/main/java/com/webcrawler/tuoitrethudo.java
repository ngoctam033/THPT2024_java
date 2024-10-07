package com.webcrawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class tuoitrethudo {
    public void fetchScores() {
        int id = 37000001; 
        for (int i = 0; i < 1000; i++) {
            id = id + i;
            String idStr = String.valueOf(id);
            String url = "https://tuoitrethudo.vn/tra-cuu-diem-thi&type_of_score=1&sbd=" + idStr;
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url);
                try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            Map<String, String> scores = new HashMap<>();
                            String html = EntityUtils.toString(entity, "UTF-8");
                            Document doc = Jsoup.parse(html);
                            Element element = doc.selectFirst("#main > div > div > div > table > tbody > tr:nth-child(7) > td > table > tbody");
                            Elements rows = element.select("tr");
                            for (Element row : rows) {
                                Elements cols = row.select("td");
                                if (cols.size() == 2) {
                                    String subject = cols.get(0).text();
                                    String score = cols.get(1).text();
                                    scores.put(subject, score);
                                    System.out.println(subject + ": " + score);
                                } else {
                                    System.out.println("No data found");
                                }
                            }
                            String json = new Gson().toJson(scores);
                            try (FileWriter file = new FileWriter("score.json")) {
                                file.write(json);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Data saved to score.json");
                            Thread.sleep(5000);
                        } else {
                            System.out.println("No content returned from the server.");
                        }
                    } else {
                        System.out.println("Failed to fetch the URL. HTTP status code: " + statusCode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}