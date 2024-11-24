package com.webcrawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Vnexpress {
    private static final String BASE_URL = "https://diemthi.vnexpress.net/index/detail/sbd/%s/year/2024";
    private static final String CSS_SELECTOR = "#warpper > div.section-content.clearfix > div.section_main.width_common > div.section-body.width_common > div > div.o-detail-thisinh > div:nth-child(2) > table > tbody";
    private static final int SLEEP_TIME_MS = 12000;

    private final String topic;
    private KafkaProducer<String, String> producer;

    public Vnexpress(String topic) {
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

    public void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = String.format(BASE_URL, idStr);

            try {
                String htmlContent = fetchHtml(url);
                Map<String, String> scores = parseScores(htmlContent, CSS_SELECTOR);
                String json = convertToJson(idStr, scores);
                System.out.println(json);
                sendToKafka(json); // Gửi đến Kafka
                Thread.sleep(SLEEP_TIME_MS);
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Error processing ID: " + idStr + " - " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }

    private String fetchHtml(String url) throws IOException {
        return WebCrawler.fetchHtml(url);
    }

    public Map<String, String> parseScores(String html, String cssSelector) throws IllegalArgumentException {
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

        Map<String, String> scores = new HashMap<>();
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

    public String convertToJson(String idStr, Map<String, String> scores) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ID", idStr);
    
        JsonObject scoreObject = new JsonObject();
        for (Map.Entry<String, String> entry : scores.entrySet()) {
            scoreObject.addProperty(entry.getKey(), entry.getValue());
        }
    
        jsonObject.add("scores", scoreObject);
        return new Gson().toJson(jsonObject);
    }

    private void sendToKafka(String json) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, json);
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            System.out.println("Message sent to topic: " + metadata.topic() +
                    " partition: " + metadata.partition() +
                    " offset: " + metadata.offset());
        } catch (Exception e) {
            System.err.println("Failed to send message to Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}