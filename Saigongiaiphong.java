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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Saigongiaiphong {

    // URL và các thông tin cố định
    private static final String BASE_URL = "https://api.sggp.org.vn/api/diem-thi?type=0&keyword=";
    private static final String QUERY_PARAMS = "&kythi=THPT&nam=2024&cumthi=0";

    // Phương thức fetchScores: lấy dữ liệu điểm thi cho từng thí sinh
    public static void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = BASE_URL + idStr + QUERY_PARAMS;

            try {
                JsonObject resultData = fetchData(url);
                if (resultData != null) {
                    Map<String, String> scores = extractScores(resultData);
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
                addScoreIfExists(result, "dm08", "Giáo dục công dân", scores);
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
