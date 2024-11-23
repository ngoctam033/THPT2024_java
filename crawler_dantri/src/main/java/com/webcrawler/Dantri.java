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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class Dantri {

    private static final String URL_TEMPLATE = "https://dantri.com.vn/thpt/1/0/99/%s/2024/0.2/search-gradle.htm";
    private static final int SLEEP_TIME_MS = 5000;

    private final String topic;
    private KafkaProducer<String, String> producer;

    public Dantri(String topic) {
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

    // Hàm fetchScores: lấy dữ liệu điểm cho từng thí sinh
    public void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = String.format(URL_TEMPLATE, idStr);

            try {
                // Lấy dữ liệu HTML từ server
                String jsonResponse = fetchHtml(url);
                if (jsonResponse != null) {
                    // Chuyển đổi dữ liệu JSON thành điểm thi
                    Map<String, String> scores = parseScores(jsonResponse);
                    if (scores != null) {
                        // Chuyển đổi điểm thi sang JSON và gửi đến Kafka
                        String json = convertToJson(idStr, scores);
                        System.out.println(json);
                        sendToKafka(json);
                    } else {
                        System.out.println("No scores found for ID: " + idStr);
                    }
                }

                // Tạm dừng 5 giây giữa các yêu cầu
                Thread.sleep(SLEEP_TIME_MS);
            } catch (Exception e) {
                System.err.println("Error processing ID: " + idStr);
                e.printStackTrace();
            }
        }
    }

    // Hàm fetchHtml: thực hiện yêu cầu HTTP để lấy dữ liệu từ server
    public static String fetchHtml(String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                } else {
                    System.out.println("Failed to fetch the URL. HTTP status code: " + statusCode);
                    return null;
                }
            }
        }
    }

    // Hàm parseScores: phân tích dữ liệu JSON và trả về điểm thi dưới dạng Map
    public static Map<String, String> parseScores(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject student = jsonObject.getAsJsonObject("student");

        if (student != null) {
            Map<String, String> scores = new HashMap<>();
            scores.put("Số báo danh", student.get("sbd").getAsString());

            // Thêm điểm vào map nếu có trong dữ liệu
            putIfPresent(scores, "Toán", student, "toan");
            putIfPresent(scores, "Ngữ văn", student, "van");
            putIfPresent(scores, "Ngoại ngữ", student, "ngoaiNgu");
            putIfPresent(scores, "Vật lý", student, "vatLy");
            putIfPresent(scores, "Hóa học", student, "hoaHoc");
            putIfPresent(scores, "Sinh học", student, "sinhHoc");
            putIfPresent(scores, "Lịch sử", student, "lichSu");
            putIfPresent(scores, "Địa lý", student, "diaLy");
            putIfPresent(scores, "GDCD", student, "gdcd");
            putIfPresent(scores, "Điểm trung bình tự nhiên", student, "diemTBTuNhien");
            putIfPresent(scores, "Điểm trung bình xã hội", student, "diemTBXaHoi");

            return scores;
        }

        return null;
    }

    // Hàm putIfPresent: thêm điểm vào map nếu có trong dữ liệu JSON
    private static void putIfPresent(Map<String, String> map, String key, JsonObject student, String jsonKey) {
        if (student.has(jsonKey) && !student.get(jsonKey).isJsonNull()) {
            map.put(key, student.get(jsonKey).getAsString());
        }
    }

    // Lớp nội bộ để đại diện cho cấu trúc JSON
    private static class ScoreResult {
        private String ID;
        private Map<String, String> score;

        public ScoreResult(String ID, Map<String, String> score) {
            this.ID = ID;
            this.score = score;
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