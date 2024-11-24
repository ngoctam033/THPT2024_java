package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.example.demo.service.StatisticsService;


@Controller
public class HomeController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/")
    public String index(Model model) {
        String chartHtml = statisticsService.generateChart();
        model.addAttribute("chartHtml", chartHtml);
        return "index"; // Trả về templates/index.html
    }
}