package com.webcrawler;

import com.webcrawler.Tuoitrethudo;

public class Main {
    public static void main(String[] args) {
        Tuoitrethudo crawler = new Tuoitrethudo("score");
        crawler.fetchScores(37000001, 1000);
    }
}