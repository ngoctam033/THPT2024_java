package com.webcrawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class WebCrawler{

    // Phương thức tĩnh để lấy HTML từ URL
    public static String fetchHtml(String url) throws IOException {
        // phương thức fetchHtml sử dụng để lấy html
        // input là url cần lấy html
        // output là chuỗi html

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // tạo một đối tượng CloseableHttpClient để thực hiện request
            // tạo một đối tượng HttpGet với url cần lấy html
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toString(entity, "UTF-8");
                    } else {
                        throw new IOException("No content returned from the server.");
                    }
                } else {
                    throw new IOException("Failed to fetch the URL. HTTP status code: " + statusCode);
                }
            }
        }
    }
}