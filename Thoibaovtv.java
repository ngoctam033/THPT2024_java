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

public class Thoibaovtv {
    
    private static final String URL_TEMPLATE = "https://vtvapi3.vtv.vn/handlers/timdiemthi.ashx?keywords=";

    // Hàm fetchScores: lấy dữ liệu điểm cho từng thí sinh từ ID bắt đầu đến số lượng bản ghi
    public static void fetchScores(int idStart, int numRecords) {
        for (int i = 0; i < numRecords; i++) {
            int currentId = idStart + i;
            String idStr = String.valueOf(currentId);
            String url = URL_TEMPLATE + idStr;

            try {
                // Fetch the HTML content from the server
                String jsonResponse = fetchHtml(url);
                if (jsonResponse != null) {
                    // Parse the JSON response and extract the scores
                    Map<String, String> scores = parseScores(jsonResponse);
                    if (scores != null) {
                        // Save the extracted scores to a file
                        saveToFile(scores);
                        System.out.println("Scores saved for ID: " + idStr);
                    } else {
                        System.out.println("No scores found for ID: " + idStr);
                    }
                }

                // Pause for 5 seconds between requests to avoid overloading the server
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

    // Hàm parseScores: phân tích dữ liệu JSON để lấy điểm thi của thí sinh
    public static Map<String, String> parseScores(String jsonResponse) {
        JsonArray dataArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
        
        if (dataArray != null && dataArray.size() > 0) {
            JsonObject student = dataArray.get(0).getAsJsonObject(); // Lấy kết quả đầu tiên
            Map<String, String> scores = new HashMap<>();
            String sbd = student.get("SOBAODANH").getAsString();
            scores.put("Số báo danh", sbd);

            // Thêm điểm cho các môn học nếu có trong dữ liệu
            putIfPresent(scores, "Toán", student, "TOAN");
            putIfPresent(scores, "Ngữ văn", student, "VAN");
            putIfPresent(scores, "Ngoại ngữ", student, "NGOAI_NGU");
            putIfPresent(scores, "Vật lý", student, "LY");
            putIfPresent(scores, "Hóa học", student, "HOA");
            putIfPresent(scores, "Sinh học", student, "SINH");
            putIfPresent(scores, "Lịch sử", student, "SU");
            putIfPresent(scores, "Địa lý", student, "DIA");
            putIfPresent(scores, "Giáo dục công dân", student, "GIAO_DUC_CONG_DAN");

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
