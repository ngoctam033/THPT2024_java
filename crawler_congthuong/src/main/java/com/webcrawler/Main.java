package com.webcrawler;

public class Main {
    public static void main(String[] args) {
        Congthuong crawler = new Congthuong("score");
        crawler.fetchScores(37000001, 1000);
    }
}