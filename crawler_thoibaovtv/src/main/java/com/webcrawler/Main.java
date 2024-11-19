package com.webcrawler;

import com.webcrawler.Thoibaovtv;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Thoibaovtv
        Thoibaovtv thoibaovtvCrawler = new Thoibaovtv("score");

        // Gọi phương thức fetchScores từ đối tượng này
        thoibaovtvCrawler.fetchScores(37000001, 1000);
    }
}