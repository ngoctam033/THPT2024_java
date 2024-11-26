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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class Saigongiaiphong {

    private static final String BASE_URL = "https://api.sggp.org.vn/api/diem-thi?type=0&keyword=";
    private static final String QUERY_PARAMS = "&kythi=THPT&nam=2024&cumthi=0";
    private static final int SLEEP_TIME_MS = 12000;

    private final String topic;
    private KafkaProducer<String, String> producer;

    public Saigongiaiphong(String topic) {
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

    // Hàm fetchScores: lấy dữ liệu điểm thi cho từng thí sinh
    public void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = BASE_URL + idStr + QUERY_PARAMS;

            try {
                JsonObject resultData = fetchData(url);
                if (resultData != null) {
                    Map<String, String> scores = extractScores(resultData);
                    String json = convertToJson(idStr, scores);
                    System.out.println(json);
                    sendToKafka(json);
                } else {
                    System.out.println("No data found for ID: " + idStr);
                }
            } catch (Exception e) {
                System.err.println("Error fetching scores for ID " + idStr + ": " + e.getMessage());
            }

            try {
                // Tạm dừng n giây giữa các yêu cầu
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }

    // Hàm fetchData: thực hiện yêu cầu HTTP và trả về dữ liệu JSON
    private static JsonObject fetchData(String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String jsonResponse = EntityUtils.toString(entity, "UTF-8");
                        return JsonParser.parseString(jsonResponse).getAsJsonObject();
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

    // Hàm extractScores: trích xuất điểm từ dữ liệu JSON
    private static Map<String, String> extractScores(JsonObject resultData) {
        Map<String, String> scores = new HashMap<>();
        JsonObject dataObject = resultData.getAsJsonObject("data");

        if (dataObject != null && dataObject.has("results")) {
            JsonArray resultsArray = dataObject.getAsJsonArray("results");

            if (resultsArray.size() > 0) {
                JsonObject result = resultsArray.get(0).getAsJsonObject();
                String sbd = result.get("sbd").getAsString();
                scores.put("Số báo danh", sbd);

                // Lấy điểm cho từng môn học
                addScoreIfExists(result, "dm01", "Toán", scores);
                addScoreIfExists(result, "dm02", "Ngữ văn", scores);
                addScoreIfExists(result, "dm03", "Vật lý", scores);
                addScoreIfExists(result, "dm04", "Hóa học", scores);
                addScoreIfExists(result, "dm05", "Sinh học", scores);
                addScoreIfExists(result, "dm06", "Lịch sử", scores);
                addScoreIfExists(result, "dm07", "Địa lý", scores); 
                addScoreIfExists(result, "dm08", "GDCD", scores);
                addScoreIfExists(result, "dm09", "Ngoại ngữ", scores); 
                addScoreIfExists(result, "dm10", "Môn khác", scores); 
            }
        }
        return scores;
    }

    // Hàm addScoreIfExists: thêm điểm vào bản đồ nếu điểm không phải là -1.0
    private static void addScoreIfExists(JsonObject result, String key, String subject, Map<String, String> scores) {
        if (result.has(key)) {
            String scoreValue = result.get(key).getAsString();
            if (!scoreValue.equals("-1.0")) {
                scores.put(subject, scoreValue);
            }
        }
    }

    // Lớp nội bộ để đại diện cho cấu trúc JSON
    private static class ScoreResult {
        private String ID;
        private Map<String, String> scores;

        public ScoreResult(String ID, Map<String, String> scores) {
            this.ID = ID;
            this.scores = scores;
        }
    }

    // Hàm convertToJson: chuyển map thành chuỗi JSON
    public static String convertToJson(String id, Map<String, String> scores) {
        // Tạo một bản sao của map và loại bỏ "Số báo danh"
        Map<String, String> scoreMap = new HashMap<>(scores);
        scoreMap.remove("Số báo danh");

        ScoreResult result = new ScoreResult(id, scoreMap);
        return new Gson().toJson(result);
    }

    // Hàm sendToKafka: gửi dữ liệu đến Kafka
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