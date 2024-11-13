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

public class Dantri {

    private static final String URL_TEMPLATE = "https://dantri.com.vn/thpt/1/0/99/%s/2024/0.2/search-gradle.htm";

    // Hàm fetchScores: lấy dữ liệu điểm cho từng thí sinh
    public static void fetchScores(int idStart, int numRecords) {
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
                        // Ghi điểm thi vào tệp
                        saveToFile(scores);
                        System.out.println("Scores saved for ID: " + idStr);
                    } else {
                        System.out.println("No scores found for ID: " + idStr);
                    }
                }

                // Tạm dừng 5 giây giữa các yêu cầu
                Thread.sleep(5000);
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
            putIfPresent(scores, "Giáo dục công dân", student, "gdcd");
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

    // Hàm saveToFile: lưu điểm vào tệp JSON
    public static void saveToFile(Map<String, String> scores) {
        try (FileWriter file = new FileWriter("score.json", true)) { // Append to file
            Gson gson = new Gson();
            String json = gson.toJson(scores);
            file.write(json + "\n");
            file.flush(); // Đảm bảo dữ liệu được ghi ra tệp
            System.out.println("Data saved to score.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
