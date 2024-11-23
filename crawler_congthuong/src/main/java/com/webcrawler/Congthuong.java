package com.webcrawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

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

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class Congthuong {

    // URL và CSS Selector
    private static final String BASE_URL = "https://congthuong.vn/tra-cuu-diem-thi&type_of_score=1&sbd=";
    private static final String CSS_SELECTOR = "#main > div.main-body.fw.lt.clearfix > div > div > table > tbody > tr:nth-child(7) > td > table > tbody";
    private static final int SLEEP_TIME_MS = 5000;

    private final String topic;
    private KafkaProducer<String, String> producer;

    public Congthuong(String topic) {
        this.topic = topic;
        initializeKafkaProducer();
    }

    private void initializeKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    // Hàm fetchScores: duyệt qua từng thí sinh để lấy điểm
    public void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = BASE_URL + idStr;

            try {
                String htmlContent = fetchHtml(url);
                if (htmlContent != null) {
                    Map<String, String> scores = parseScores(htmlContent, CSS_SELECTOR);
                    String json = convertToJson(idStr, scores);
                    System.out.println(json);
                    sendToKafka(json); // Gửi đến Kafka
                }
            } catch (Exception e) {
                System.err.println("Error processing ID " + idStr + ": " + e.getMessage());
                e.printStackTrace();
            }

            try {
                // Tạm dừng 5 giây giữa mỗi lần lấy dữ liệu
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }

    // Hàm fetchHtml: lấy nội dung HTML từ URL
    public static String fetchHtml(String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toString(entity, "UTF-8");
                    } else {
                        System.out.println("No content returned from the server.");
                    }
                } else {
                    System.out.println("Failed to fetch the URL. HTTP status code: " + statusCode);
                }
            }
        }
        return null;
    }

    // Hàm parseScores: phân tích HTML để lấy điểm và lưu vào map
    public static Map<String, String> parseScores(String html, String cssSelector) throws IllegalArgumentException {
        Map<String, String> scores = new HashMap<>();

        Document doc = Jsoup.parse(html);
        Element element = doc.selectFirst(cssSelector);

        if (element != null) {
            Elements rows = element.select("tr");
            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() == 2) {
                    String subject = cols.get(0).text();
                    String score = cols.get(1).text();
                    scores.put(subject, score);
                } else {
                    System.out.println("No data found");
                }
            }
        } else {
            throw new IllegalArgumentException("No element found for the given CSS selector");
        }

        return scores;
    }

    // Lớp nội bộ để đại diện cho cấu trúc JSON
    private static class ScoreResult {
        private String ID;
        private Map<String, String> score;

        public ScoreResult(String ID, Map<String, String> Score) {
            this.ID = ID;
            this.score = Score;
        }
    }

    // Hàm convertToJson: chuyển map thành chuỗi JSON
    public static String convertToJson(String id, Map<String, String> scores) {
        ScoreResult result = new ScoreResult(id, scores);
        return new Gson().toJson(result);
    }

    // Hàm sendToKafka: gửi dữ liệu đến Kafka
    private void sendToKafka(String json) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, json);
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
        } catch (Exception e) {
            System.err.println("Failed to send message to Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}