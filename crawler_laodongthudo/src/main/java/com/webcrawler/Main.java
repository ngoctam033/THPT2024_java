package com.webcrawler;

import com.webcrawler.Laodongthudo;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Laodongthudo
        Laodongthudo laodongthudoCrawler = new Laodongthudo("thpt_2024");

        // Gọi phương thức fetchScores từ đối tượng này
        laodongthudoCrawler.fetchScores(37000001, 1000);
    }
}