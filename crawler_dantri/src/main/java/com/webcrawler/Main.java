package com.webcrawler;

public class Main {
    public static void main(String[] args) {
        Dantri crawler = new Dantri("score");
        crawler.fetchScores(37000001, 1000);
    }
}