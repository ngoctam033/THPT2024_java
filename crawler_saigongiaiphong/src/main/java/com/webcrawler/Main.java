package com.webcrawler;

import com.webcrawler.Saigongiaiphong;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Saigongiaiphong
        Saigongiaiphong saigongiaiphongCrawler = new Saigongiaiphong("score");

        // Gọi phương thức fetchScores từ đối tượng này
        saigongiaiphongCrawler.fetchScores(37000001, 1000);
    }
}