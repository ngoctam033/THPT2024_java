package com.webcrawler;

import com.webcrawler.Vnexpress;

public class Main {
    public static void main(String[] args) {
        Vnexpress crawler = new Vnexpress("thpt_2024");
        crawler.fetchScores(37016059, 2000);
    }
}