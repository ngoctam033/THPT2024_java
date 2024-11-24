package com.webcrawler;

public class Main {
    public static void main(String[] args) {
        Dantri crawler = new Dantri("thpt_2024");
        crawler.fetchScores(37002062, 2000);
    }
}