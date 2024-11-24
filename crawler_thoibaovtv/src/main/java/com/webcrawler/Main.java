package com.webcrawler;

import com.webcrawler.Thoibaovtv;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Thoibaovtv
        Thoibaovtv thoibaovtvCrawler = new Thoibaovtv("thpt_2024");

        // Gọi phương thức fetchScores từ đối tượng này
        thoibaovtvCrawler.fetchScores(37012062, 2000);
    }
}