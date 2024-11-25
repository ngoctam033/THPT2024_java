package com.webcrawler;

import com.webcrawler.Saigongiaiphong;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Saigongiaiphong
        Saigongiaiphong saigongiaiphongCrawler = new Saigongiaiphong("thpt_2024");

        // Gọi phương thức fetchScores từ đối tượng này
        saigongiaiphongCrawler.fetchScores(37008001, 2000);
    }
}