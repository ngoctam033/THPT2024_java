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

public class Tuoitrethudo {
    // thuộc tính url
    private String url;
    // thuộc tính cssSelector
    private String cssSelector;
    // topic Kafka
    private String topic;
    // hàm khởi tạo
    public Tuoitrethudo(String topic) {
        this.url = "https://tuoitrethudo.vn/tra-cuu-diem-thi&type_of_score=1&sbd=";
        this.cssSelector = "#main > div > div > div > table > tbody > tr:nth-child(7) > td > table > tbody";
        this.topic = topic;
    }

    // hàm fetchScores, input là mã tỉnh, số lượng điểm cần lấy
        // hàm fetchScores, input là mã tỉnh, số lượng điểm cần lấy
        public void fetchScores(int idStart, int numRecords) {
            // vòng lặp để lấy điểm của từng thí sinh
            for (int i = 0; i < numRecords; i++) {
                // tạo số báo danh
                int currentId = idStart + i;
                // chuyển đối số báo danh sang chuỗi
                String idStr = String.valueOf(currentId);   
                // tạo link url cần lấy điểm
                String url = this.url + idStr;
                String htmlContent = null;
                Map<String, String> scores = null;
                try {
                    // truy cập vào url và lấy html
                    htmlContent = WebCrawler.fetchHtml(url);
                    // In nội dung HTML ra màn hình
                    System.out.println(htmlContent);
                } catch (IOException e) {
                    // Xử lý ngoại lệ nếu có lỗi xảy ra
                    System.err.println("Error fetching HTML content: " + e.getMessage());
                    e.printStackTrace();
                    continue; // Bỏ qua thí sinh này và tiếp tục với thí sinh tiếp theo
                }
    
                try {
                    // Gọi phương thức parseScores
                    scores = parseScores(htmlContent, cssSelector);
    
                    // Xử lý kết quả
                    System.out.println("Parsed scores:");
                    for (Map.Entry<String, String> entry : scores.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                } catch (IllegalArgumentException e) {
                    // Xử lý ngoại lệ
                    System.err.println("Error parsing scores: " + e.getMessage());
                    e.printStackTrace();
                    continue; // Bỏ qua thí sinh này và tiếp tục với thí sinh tiếp theo
                }
    
                // chuyển điểm sang json
                String json = convertToJson(scores);
                // in json
                System.out.println(json);
    
                // // gửi đến kafka
                // Future<RecordMetadata> future = KafkaUtils.sendJsonToKafka(this.topic, json);
    
                // // Kiểm tra kết quả
                // if (future != null) {
                //     try {
                //         RecordMetadata metadata = future.get();
                //         System.out.println("Message sent to topic: " + metadata.topic() +
                //                         " partition: " + metadata.partition() +
                //                         " offset: " + metadata.offset());
                //     } catch (Exception e) {
                //         System.err.println("Failed to get record metadata: " + e.getMessage());
                //         e.printStackTrace();
                //     }
                // } else {
                //     System.err.println("Failed to send message to Kafka.");
                // }
            }
        }
    public Map<String, String> parseScores(String html, String cssSelector) throws IllegalArgumentException {
        Map<String, String> scores = new HashMap<>();
        
        if (html == null || html.isEmpty()) {
            throw new IllegalArgumentException("HTML content is null or empty");
        }
        
        if (cssSelector == null || cssSelector.isEmpty()) {
            throw new IllegalArgumentException("CSS selector is null or empty");
        }

        Document doc;
        try {
            doc = Jsoup.parse(html);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse HTML content", e);
        }

        Element element = doc.selectFirst(cssSelector);
        if (element == null) {
            throw new IllegalArgumentException("No element found for the given CSS selector");
        }

        Elements rows = element.select("tr");
        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() == 2) {
                String subject = cols.get(0).text();
                String score = cols.get(1).text();
                scores.put(subject, score);
            } else {
                throw new IllegalArgumentException("Unexpected number of columns in row: " + cols.size());
            }
        }
        
        return scores;
    }

    // tạo methos để convert map sang json
    public String convertToJson(Map<String, String> scores) {
        return new Gson().toJson(scores);
    }
}