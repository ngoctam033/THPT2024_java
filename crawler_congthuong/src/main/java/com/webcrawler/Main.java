package com.webcrawler;

public class Main {
    public static void main(String[] args) {
        Congthuong crawler = new Congthuong("thpt_2024");
        crawler.fetchScores(37004686, 500);
    }
}