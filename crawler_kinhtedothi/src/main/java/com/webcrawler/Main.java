package com.webcrawler;

public class Main {
    public static void main(String[] args) {
        // Tạo một đối tượng của lớp Kinhtedothi
        Kinhtedothi kinhtedothiCrawler = new Kinhtedothi("score");

        // Gọi phương thức fetchScores từ đối tượng này
        kinhtedothiCrawler.fetchScores(37000001, 1000);
    }
}