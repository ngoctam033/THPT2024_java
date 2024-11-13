package com.webcrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Kinhtedothi {

    // URL gốc
    private static final String BASE_URL = "https://diem.kinhtedothi.vn/thpt/";

    // Phương thức fetchScores: lấy dữ liệu điểm thi cho từng thí sinh
    public static void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = BASE_URL + idStr;

            try {
                JsonObject studentData = fetchData(url);
                if (studentData != null) {
                    Map<String, String> scores = extractScores(studentData, idStr);
                    saveToFile(scores);
                    System.out.println("Scores saved for ID: " + idStr);
                } else {
                    System.out.println("No data found for ID: " + idStr);
                }
            } catch (Exception e) {
                System.err.println("Error fetching scores for ID " + idStr + ": " + e.getMessage());
            }

            try {
                // Tạm dừng 5 giây giữa các yêu cầu
                Thread.sleep(5000);
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
    private static Map<String, String> extractScores(JsonObject studentData, String idStr) {
        Map<String, String> scores = new HashMap<>();
        scores.put("Số báo danh", idStr);

        // Trích xuất điểm từ các môn học
        addScoreIfExists(studentData, "toan", "Toán", scores);
        addScoreIfExists(studentData, "van", "Ngữ văn", scores);
        addScoreIfExists(studentData, "ly", "Vật lý", scores);
        addScoreIfExists(studentData, "hoa", "Hóa học", scores);
        addScoreIfExists(studentData, "sinh", "Sinh học", scores);
        addScoreIfExists(studentData, "su", "Lịch sử", scores);
        addScoreIfExists(studentData, "dia", "Địa lý", scores);
        addScoreIfExists(studentData, "cd", "Giáo dục công dân", scores);
        addScoreIfExists(studentData, "nn", "Ngoại ngữ", scores);
        addScoreIfExists(studentData, "mnn", "Mã Ngoại ngữ", scores);

        return scores;
    }

    // Hàm addScoreIfExists: thêm điểm vào bản đồ nếu điểm không phải là rỗng
    private static void addScoreIfExists(JsonObject studentData, String key, String subject, Map<String, String> scores) {
        if (studentData.has(key) && !studentData.get(key).getAsString().isEmpty()) {
            String scoreValue = studentData.get(key).getAsString();
            scores.put(subject, scoreValue);
        }
    }

    // Hàm saveToFile: lưu điểm vào tệp JSON
    private static void saveToFile(Map<String, String> scores) {
        try (FileWriter file = new FileWriter("score.json", true)) { // Append to file
            Gson gson = new Gson();
            String json = gson.toJson(scores);
            file.write(json + "\n");
            file.flush(); // Đảm bảo dữ liệu được ghi ra tệp
            System.out.println("Data saved to score.json");
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }
}
